package hessian.scoreaas.model;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.jpmml.evaluator.visitors.ElementInternerBattery;
import org.jpmml.evaluator.visitors.ElementOptimizerBattery;
import org.jpmml.model.PMMLUtil;
import org.jpmml.model.VisitorBattery;
import org.jpmml.model.visitors.AttributeInternerBattery;
import org.jpmml.model.visitors.AttributeOptimizerBattery;
import org.jpmml.model.visitors.ListFinalizerBattery;
import org.jpmml.model.visitors.LocatorNullifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.*;

@RestController
public class ModelController {
    @Autowired
    private ModelDao repository;
    private Map<NameVersion,Evaluator> modelCache = new HashMap<NameVersion,Evaluator>();
    private ModelEvaluatorFactory modelEvaluatorFactory;
    private VisitorBattery visitorBattery;

    @PostConstruct
    private void initialize() {
        this.modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        ValueFactoryFactory valueFactoryFactory = ReportingValueFactoryFactory.newInstance();
        this.visitorBattery = new VisitorBattery();

        visitorBattery.add(LocatorNullifier.class);
        visitorBattery.addAll(new AttributeOptimizerBattery());
        visitorBattery.addAll(new ElementOptimizerBattery());
        visitorBattery.addAll(new AttributeInternerBattery());
        visitorBattery.addAll(new ElementInternerBattery());
        visitorBattery.addAll(new ListFinalizerBattery());
    }

    @RequestMapping("/model/hello")
    @ResponseBody
    public String hello() {
        return "\n<html><body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<table>" +
                "\n <tr><td><b>Save a model</b></td> <td>/model/save?model_name=[String]&model_version=[Integer]&model=[String]</td></tr>" +
                "\n <tr><td><b>Delete a model</b></td> <td> /model/delete?model_name=[String]&model_version=[Integer]</td></tr>" +
                "\n <tr><td><b>Delete all versions of a model</b></td> <td>/model/deleteAll?model_name=[String]</td></tr>" +
                "\n <tr><td><b>List all models and versions</b></td> <td>/model/listAll</td></tr>" +
                "\n <tr><td><b>List all versions of a model</b></td> <td>/model/listModelVersions?model_name=[String]</td></tr>" +
                "\n <tr><td><b>List a version of a model</b></td> <td>/model/listModel?model_name=[String]&model_version=[Integer]</td></tr>" +
                "\n <tr><td><b>Score a model</b></td> <td>/model/score?model_name=[String]&model_version=[Integer]&model_input1=[Value](&model_input2=[Value]...)</td></tr>" +
                "\n</table>" +
                "\n</body></html>";
    }

    // SAVE
    @RequestMapping(value = "/model/save", method = {RequestMethod.GET, RequestMethod.POST})
    public Model createMode(@RequestParam String model_name, @RequestParam Integer model_version, @RequestParam String model) {
        return repository.save(new Model(model_name, model_version, model));
    }

    // DELETE
    @RequestMapping(value = "/model/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public void delete(@RequestParam String model_name, @RequestParam Integer model_version) {
        repository.delete(model_name, model_version);
    }

    @RequestMapping(value = "/model/deleteAll", method = {RequestMethod.GET, RequestMethod.POST})
    public void delete(@RequestParam String model_name) {
        repository.deleteAll(model_name);
    }

    // LIST
    @RequestMapping(value = "/model/listAll", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Model> listAll() {
        return repository.findAll().all();
    }

    @RequestMapping(value = "/model/listModelVersions", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Model> list(@RequestParam String model_name) {
        return repository.findByModelName(model_name).all();
    }

    @RequestMapping(value = "/model/listModel", method = {RequestMethod.GET, RequestMethod.POST})
    public Model list(@RequestParam String model_name, @RequestParam Integer model_version) {
        return repository.findByModelNameAndModelVersion(model_name, model_version);
    }

    // SCORE
    @RequestMapping(value = "/model/score", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String,Object> score(@RequestParam Map<String,String> params) {
        if (!params.containsKey("model_name")) {
            //throw new IllegalArgumentException("Must specify model_name");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must specify model_name");
        }
        String model_name = params.get("model_name");
        if (!params.containsKey("model_version")) {
            //throw new IllegalArgumentException("Must specify model_version");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must specify model_version");
        }
        Integer model_version = Integer.parseInt(params.get("model_version"));
        Evaluator evaluator = findOrReadModel(model_name, model_version);

        return applyModel(evaluator, params);
    }

    private Evaluator findOrReadModel(String model_name, Integer model_version) {
        return modelCache.getOrDefault(new NameVersion(model_name, model_version),
                                       getEvaluator(repository.findByModelNameAndModelVersion(model_name, model_version)));
    }

    private Evaluator getEvaluator(Model model) {
        PMML pmml = null;
        try {
            pmml = PMMLUtil.unmarshal(new ByteArrayInputStream(model.getModel().getBytes()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        visitorBattery.applyTo(pmml);
        Evaluator evaluator = (Evaluator)modelEvaluatorFactory.newModelEvaluator(pmml);
        evaluator.verify();
        modelCache.put(new NameVersion(model.getModel_name(), model.getModel_version()), evaluator);

        return evaluator;
    }

    private Map<String,Object> applyModel(Evaluator evaluator, Map<String,String> args) {
        // Get Arguments
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        List<InputField> inputFields = evaluator.getInputFields();
        List<String> notFound = new ArrayList<String>();
        for(InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            if (args.containsKey(inputFieldName.toString())) {
                FieldValue inputFieldValue = inputField.prepare(args.get(inputFieldName.toString()));
                arguments.put(inputFieldName, inputFieldValue);
            }
            else {
                notFound.add(inputFieldName.toString());
            }
        }

        if (0 < notFound.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing some inputs: " + String.join(", ", notFound));
        }
        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        Map<String,Object> output = new HashMap<String, Object>();
        results.forEach((k,v) -> output.put(k.toString(), v));

        return output;
    }

    protected class NameVersion {
        private String name;
        private Integer version;

        public NameVersion() { }
        private NameVersion(String name, Integer version ) {
            this.name = name;
            this.version = version;
        }

        private String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private Integer getVersion() {
            return version;
        }

        private void setVersion(Integer version) {
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NameVersion)) return false;
            NameVersion that = (NameVersion) o;
            return Objects.equals(getName(), that.getName()) &&
                    Objects.equals(getVersion(), that.getVersion());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getVersion());
        }
    }
}

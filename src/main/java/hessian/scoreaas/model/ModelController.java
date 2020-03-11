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
    protected ModelDao repository;
    protected ModelUtils modelUtils;

    @PostConstruct
    private void initialize() {
        modelUtils = new ModelUtils();
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
                "\n <tr><td><b>List the fields of a model</b></td> <td>/model/listFields?model_name=[String]&model_version=[Integer]</td></tr>" +
                "\n <tr><td><b>List the details of the fields of a model</b></td> <td>/model/listFieldDetails?model_name=[String]&model_version=[Integer]</td></tr>" +
                "\n <tr><td><b>Score a model</b></td> <td>/model/score?model_name=[String]&model_version=[Integer]&model_input1=[Value](&model_input2=[Value]...)</td></tr>" +
                "\n</table>" +
                "\n</body></html>";
    }

    // SAVE
    @RequestMapping(value = "/model/save", method = {RequestMethod.GET, RequestMethod.POST})
    public Model createModel(@RequestParam String model_name, @RequestParam Integer model_version, @RequestParam String model) {
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

    @RequestMapping(value = "/model/listFieldDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, List> listFieldDetails(@RequestParam String model_name, @RequestParam Integer model_version) {
        Map<String,List> insAndOuts = new TreeMap<String,List>();
        Evaluator evaluator = findOrReadModel(model_name, model_version);
        insAndOuts.put("Input", evaluator.getInputFields());
        insAndOuts.put("Output", evaluator.getOutputFields());
        insAndOuts.put("Target", evaluator.getTargetFields());
        insAndOuts.put("Active", evaluator.getActiveFields());
        return insAndOuts;
    }

    @RequestMapping(value = "/model/listFields", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, List> listFields(@RequestParam String model_name, @RequestParam Integer model_version) {
        Map<String,List> insAndOuts = new TreeMap<String,List>();
        Evaluator evaluator = findOrReadModel(model_name, model_version);
        insAndOuts.put("Input", modelUtils.listInputs(evaluator.getInputFields()));
        insAndOuts.put("Output", modelUtils.listOutputs(evaluator.getOutputFields()));
        insAndOuts.put("Target", modelUtils.listTargets(evaluator.getTargetFields()));
        insAndOuts.put("Active", modelUtils.listInputs(evaluator.getActiveFields()));
        return insAndOuts;
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

        return modelUtils.applyModel(evaluator, params);
    }

    protected Evaluator findOrReadModel(String model_name, Integer model_version) {
        return modelUtils.getOrDefault(new NameVersion(model_name, model_version),
                modelUtils.getEvaluator(repository.findByModelNameAndModelVersion(model_name, model_version)));
    }

}

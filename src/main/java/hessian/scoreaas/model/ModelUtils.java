package hessian.scoreaas.model;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Target;
import org.jpmml.evaluator.*;
import org.jpmml.evaluator.visitors.ElementInternerBattery;
import org.jpmml.evaluator.visitors.ElementOptimizerBattery;
import org.jpmml.model.PMMLUtil;
import org.jpmml.model.VisitorBattery;
import org.jpmml.model.visitors.AttributeInternerBattery;
import org.jpmml.model.visitors.AttributeOptimizerBattery;
import org.jpmml.model.visitors.ListFinalizerBattery;
import org.jpmml.model.visitors.LocatorNullifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.*;

public class ModelUtils {
    protected Map<NameVersion,Evaluator> modelCache = new HashMap<NameVersion,Evaluator>();
    protected ModelEvaluatorFactory modelEvaluatorFactory;
    protected VisitorBattery visitorBattery;

    public ModelUtils() {
        initialize();
    }

    @PostConstruct
    private void initialize() {
        this.modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        this.visitorBattery = new VisitorBattery();

        visitorBattery.add(LocatorNullifier.class);
        visitorBattery.addAll(new AttributeOptimizerBattery());
        visitorBattery.addAll(new ElementOptimizerBattery());
        visitorBattery.addAll(new AttributeInternerBattery());
        visitorBattery.addAll(new ElementInternerBattery());
        visitorBattery.addAll(new ListFinalizerBattery());
    }

    protected List<Vitals> listInputs(List<InputField> fields) {
        List<Vitals> items = new ArrayList<Vitals>();
        for (InputField field : fields) {
            items.add(getVitals(field));
        }
        return items;
    }

    protected Vitals getVitals(InputField field) {
        Vitals vitals = new Vitals();
        vitals.setName((null != field.getName()) ? field.getName().toString() : null);
        vitals.setOpType((null != field.getOpType()) ? field.getOpType().name() : null);
        vitals.setDataType((null != field.getDataType()) ? field.getDataType().name() : null);
        return vitals;
    }

    protected List<Vitals> listOutputs(List<OutputField> fields) {
        List<Vitals> items = new ArrayList<Vitals>();
        for (OutputField field : fields) {
            items.add(getVitals(field));
        }
        return items;
    }

    protected Vitals getVitals(OutputField field) {
        Vitals vitals = new Vitals();
        vitals.setName((null != field.getName()) ? field.getName().toString() : null);
        vitals.setOpType((null != field.getOpType()) ? field.getOpType().name() : null);
        vitals.setDataType((null != field.getDataType()) ? field.getDataType().name() : null);
        return vitals;
    }

    protected List<TargetVitals> listTargets(List<TargetField> fields) {
        List<TargetVitals> items = new ArrayList<TargetVitals>();
        for (TargetField field : fields) {
            items.add(getVitals(field));
        }
        return items;
    }

    protected TargetVitals getVitals(TargetField field) {
        TargetVitals vitals = new TargetVitals();
        vitals.setName((null != field.getName()) ? field.getName().toString() : null);
        vitals.setOpType((null != field.getOpType()) ? field.getOpType().name() : null);
        vitals.setDataType((null != field.getDataType()) ? field.getDataType().name() : null);
        vitals.setCategories((null != field.getCategories()) ? field.getCategories() : null);
        return vitals;
    }

    protected Evaluator getEvaluator(Model model) {
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

    protected Evaluator getOrDefault(NameVersion nameVersion, Evaluator evaluator) {
        return modelCache.getOrDefault(nameVersion, evaluator);
    }

    protected Map<String,Object> applyModel(Evaluator evaluator, Map<String,String> args) {
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


    public class Vitals {
        private String name;
        private String opType;
        private String dataType;

        public Vitals() { }

        public Vitals(String name, String opType, String dataType) {
            this.name = name;
            this.opType = opType;
            this.dataType = dataType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOpType() {
            return opType;
        }

        public void setOpType(String opType) {
            this.opType = opType;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }

    public class TargetVitals extends Vitals {
        private List<String> categories;

        public TargetVitals() { super(); }

        public TargetVitals(String name, String opType, String dataType, List<String> categories) {
            super(name, opType, dataType);
            this.categories = categories;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
    }
}

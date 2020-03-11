package hessian.scoreaas.model;

import org.apache.commons.lang.StringUtils;
import org.jpmml.evaluator.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class WebController {
    @Autowired
    protected ModelDao repository;
    @Autowired
    protected ModelUtils modelUtils;

    @PostConstruct
    private void initialize() {
        modelUtils = new ModelUtils();
    }

    public String endpointList() {
        return  "\n<table>" +
                "\n <tr><td><b><a href=\"/web/save\">Save a model</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/delete\">Delete a model</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/deleteAll\">Delete all versions of a model</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/listAll\">List all models and versions</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/listModelVersions\">List all versions of a model</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/listModel\">List a version of a model</a></b></td></tr>" +
                "\n <tr><td><b><a href=\"/web/score\">Score a model</a></b></td></tr>" +
                "\n</table>";

    }

    @RequestMapping("/")
    @ResponseBody
    public String root() {
        return hello();
    }

    @RequestMapping("/web")
    @ResponseBody
    public String hello() {
        return "\n<html><body>" +
                "\n<H1>Score-aaS</H1>" +
                endpointList() +
                "\n</body></html>";
    }

    // Save
    @RequestMapping(value = "/web/save", method = {RequestMethod.GET, RequestMethod.POST})
    public String webCreateModel(@RequestParam(required = false) String model_name,
                                @RequestParam(required = false) Integer model_version,
                                @RequestParam(required = false) String model) {
        if ((null != model_name) & (null != model_version) && (null != model)) {
            repository.save(new Model(model_name, model_version, model));
            return webListAll();
        }

        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>Add Model</H2>");
        retString.append("\n<form action=\"/web/save\">");
        retString.append(String.format("\n<table>" +
                        "\n <tr><td>Model Name:</td><td><input type=\"text\" id=\"model_name\" name=\"model_name\" %s required></td></tr>" +
                        "\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\" %s required></td></tr>" +
                        "\n <tr><td>Model PMML:</td><td><textarea rows=\"20\" cols=\"50\" id=\"model\" name=\"model\" required>%s</textarea></td></tr>",
                (null == model_name) ? "" : "value=\"" + model_name + "\"",
                (null == model_version) ? "" : "value=\"" + Integer.toString(model_version) + "\"",
                (null == model) ? "" : "value=\"" + model + "\""));
        retString.append("</table>");
        retString.append("\n<input type=\"submit\" value=\"Add\">");
        retString.append("\n</form>");
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }


    // Delete
    @RequestMapping(value = "/web/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String webDeleteModel(@RequestParam(required = false) String model_name,
                                 @RequestParam(required = false) Integer model_version) {
        if ((null != model_name) & (null != model_version)) {
            repository.delete(model_name, model_version);
            return webListAll();
        }

        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>Delete Model</H2>");
        retString.append("\n<form action=\"/web/delete\">");
        retString.append("\n<table>");
        retString.append(String.format("\n <tr><td>Model Name:</td><td><input type=\"text\" id=\"model_name\" name=\"model_name\" %s required></td></tr>",
                (null == model_name) ? "" : "value=\"" + model_name + "\""));
        if (null == model_name) {
            retString.append(String.format("\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\" %s required></td></tr>",
                    (null == model_version) ? "" : "value=\"" + Integer.toString(model_version) + "\""));
        }
        else {
            List<Model> models = repository.findByModelName(model_name).all();
            if (0 == models.size()) {
                retString.append("\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\" required></td></tr>");
            }
            else {
                retString.append("\n <tr><td>Model Version:</td><td><select id=\"model_version\" name=\"model_version\" required>");
                for (Model model : models) {
                    retString.append(String.format("<option value=\"%s\">%s</option>", Integer.toString(model.getModel_version()), Integer.toString(model.getModel_version())));
                }
                retString.append("</select></td></tr>");
            }
        }
        retString.append("</table>");
        retString.append("\n<input type=\"submit\" value=\"DELETE\">");
        retString.append("\n</form>");
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }

    @RequestMapping(value = "/web/deleteAll", method = {RequestMethod.GET, RequestMethod.POST})
    public String webDeleteAll(@RequestParam(required = false) String model_name) {
        if ((null != model_name)) {
            repository.deleteAll(model_name);
            return webListAll();
        }

        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>Delete All Verions of a Model</H2>");
        retString.append("\n<form action=\"/web/deleteAll\">");
        retString.append("\n<table>" +
                        "\n <tr><td>Model Name:</td><td><select id=\"model_name\" name=\"model_name\" required>");
        List<Model> models = repository.findAll().all();
        Set<String> modelNames = models.stream().map(Model::getModel_name).collect(Collectors.toSet());
        for (String name : modelNames) {
            retString.append(String.format("<option value=\"%s\">%s</option>", name, name));
        }
        retString.append("</td></tr></table>");
        retString.append("\n<input type=\"submit\" value=\"DELETE\">");
        retString.append("\n</form>");
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }

    // List
    @RequestMapping(value = "/web/listAll", method = {RequestMethod.GET, RequestMethod.POST})
    public String webListAll() {
        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                        "  font-family: arial, sans-serif;\n" +
                        "  border-collapse: collapse;\n" +
                        "}\n" +
                        "\n" +
                        "td, th {\n" +
                        "  border: 1px solid #dddddd;\n" +
                        "  text-align: left;\n" +
                        "  padding: 8px;\n" +
                        "}\n" +
                        "</style>" +
                        "<body>" +
                        "\n<H1>Score-aaS</H1>" +
                "\n<H2>List All Models </H2>" +
                "\n<table>" +
                "\n <tr><td>Name</td><td>Version</td><td>Model</td>");
        List<Model> models = repository.findAll().all();
        models.sort((m1,m2) -> (0 == m1.getModel_name().compareTo(m2.getModel_name()))
                ? m1.getModel_version().compareTo(m2.getModel_version())
                : m1.getModel_name().compareTo(m2.getModel_name()));
        for (Model model : models) {
            Evaluator evaluator = modelUtils.getEvaluator(model);
            retString.append("<tr><td>")
                    .append("<a href=\"/web/listModelVersions?model_name=" + model.getModel_name() + "\">" + model.getModel_name() + "</a>")
                    .append("</td><td>")
                    .append("<a href=\"/web/listModel?model_name=" + model.getModel_name() + "&model_version=" + model.getModel_version() + "\">" + model.getModel_version() + "</a>")
                    .append("</td><td>")
                    .append(evaluator.getSummary())
                    .append("</td></tr>");
        }
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }

    @RequestMapping(value = "/web/listModelVersions", method = {RequestMethod.GET, RequestMethod.POST})
    public String webListModelVersions(@RequestParam(required = false) String model_name) {
        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>List Model Versions</H2>");
        // form with model_name
        retString.append("\n<form action=\"/web/listModelVersions\">");
        retString.append("\n<table>" +
                "\n <tr><td><label for=\"model_name\">Model Name:</label></td><td>");
        retString.append("<input type=\"text\" id=\"model_name\" name=\"model_name\"");
        retString.append((null == model_name) ? "" : " value=\"" + model_name + "\"");
        retString.append(">");
        retString.append("</td></tr>");
        // results
        if (null != model_name) {
            retString.append("<tr><td>Model Numbers:</td><td><table>");
            List<Model> models = repository.findByModelName(model_name).all();
            models.sort((m1,m2) -> (0 == m1.getModel_name().compareTo(m2.getModel_name()))
                    ? m1.getModel_version().compareTo(m2.getModel_version())
                    : m1.getModel_name().compareTo(m2.getModel_name()));

            if (models.size() < 1)
                retString.append("<tr><td>None found</td></tr>");
            else {
                for (Model model : models) {
                    retString.append("<tr><td>")
                            .append(String.format("<a href=/web/listModel?model_name=%s&model_version=%d>%d</a>",
                                    model.getModel_name(), model.getModel_version(), model.getModel_version()))
                            .append("</td></tr>");
                }
            }
            retString.append("</table></td></tr>");
        }
        retString.append("\n</table>");
        retString.append("\n<input type=\"submit\" value=\"Submit\">");
        retString.append("\n</form>");
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }

    @RequestMapping(value = "/web/listModel", method = {RequestMethod.GET, RequestMethod.POST})
    public String webListModel(@RequestParam(required = false) String model_name,
                               @RequestParam(required = false) Integer model_version,
                               @RequestParam(required = false) String raw) {
        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>List Model</H2>");
        retString.append("\n<form action=\"/web/listModel\">");
        retString.append(String.format("\n<table>" +
                        "\n <tr><td>Model Name:</td><td><input type=\"text\" id=\"model_name\" name=\"model_name\" %s></td></tr>" +
                        "\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\" %s></td></tr>",
                (null == model_name) ? "" : "value=\"" + model_name + "\"",
                (null == model_version) ? "" : "value=\"" + Integer.toString(model_version) + "\""));
        if ((null != model_name) && (null != model_version)) {
            Model model = repository.findByModelNameAndModelVersion(model_name, model_version);
            if (null == model) {
                retString.append("\n<tr>Model not found</tr>");
            }
            else {
                if ((null != raw) && (0 == raw.compareToIgnoreCase("yes"))) {
                    retString.append(String.format("\n <tr><td>Model PMML</td><td><textarea cols=\"80\" rows=\"40\">%s</textarea></td></tr>", model.getModel()));
                }
                else {
                    Evaluator evaluator = modelUtils.getEvaluator(model);
                    retString.append(fieldTable(evaluator));
                }
            }
        }
        retString.append("<tr><td>Raw PMML Output:</td><td><input type=\"checkbox\" id=\"raw\" name=\"raw\" value=\"yes\"" +
                (((null != raw) && (0 == raw.compareToIgnoreCase("yes"))) ? " checked=\"true\"" : "") + "></td></tr>");
        retString.append("\n</table>");
        retString.append("\n<input type=\"submit\" value=\"Submit\">");
        retString.append("\n</form>");
        if ((null != model_name) && (null != model_version)) {
            retString.append("\n<hr>");
            retString.append(String.format("\n<h3><a href=\"/web/score?model_name=%s&model_version=%s\">Score this model</a></h3>",
                    model_name, Integer.toString(model_version)));
        }
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }

    protected String fieldTable(Evaluator evaluator) {
        List<ModelUtils.Vitals> inputs = modelUtils.listInputs(evaluator.getInputFields());
        List<ModelUtils.Vitals> outputs = modelUtils.listOutputs(evaluator.getOutputFields());
        List<ModelUtils.Vitals> active = modelUtils.listInputs(evaluator.getActiveFields());
        List<ModelUtils.TargetVitals> targets = modelUtils.listTargets(evaluator.getTargetFields());

        StringBuilder retString = new StringBuilder("<table>");
        Collections.sort(inputs, (m1,m2) -> (m1.getName().compareTo(m2.getName())));
        Collections.sort(outputs, (m1,m2) -> (m1.getName().compareTo(m2.getName())));
        Collections.sort(active, (m1,m2) -> (m1.getName().compareTo(m2.getName())));
        Collections.sort(targets, (m1,m2) -> (m1.getName().compareTo(m2.getName())));

        retString.append("<tr><td>Inputs</td><td><table>");
        for (ModelUtils.Vitals field : inputs) {
            retString.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",
                    field.getName(), field.getOpType(), field.getDataType()));
        }
        retString.append("</table></td></tr>");
        retString.append("<tr><td>Outputs</td><td><table>");
        for (ModelUtils.Vitals field : outputs) {
            retString.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",
                    field.getName(), field.getOpType(), field.getDataType()));
        }
        retString.append("</table></td></tr>");
        retString.append("<tr><td>Active</td><td><table>");
        for (ModelUtils.Vitals field : active) {
            retString.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",
                    field.getName(), field.getOpType(), field.getDataType()));
        }
        retString.append("</table></td></tr>");
        retString.append("<tr><td>Targets</td><td><table>");
        for (ModelUtils.TargetVitals field : targets) {
            retString.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    field.getName(), field.getOpType(), field.getDataType(),
                    "<table><tr><td>" + StringUtils.join(field.getCategories(), "</td></tr><tr><td>") + "</td></tr></table>"));
        }
        retString.append("</table></td></tr>");

        retString.append("</table>");
        return retString.toString();
    }

    // Score
    @RequestMapping(value = "/web/score", method = {RequestMethod.GET, RequestMethod.POST})
    public String webScore(@RequestParam Map<String,String> params) {
        String model_name = params.remove("model_name");
        String model_version_string = params.remove("model_version");
        StringBuilder retString = new StringBuilder();
        retString.append("\n<html><style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "</style>" +
                "<body>" +
                "\n<H1>Score-aaS</H1>" +
                "\n<H2>Score Model</H2>");
        retString.append("\n<form action=\"/web/score\">");
        retString.append(String.format("\n<table>" +
                        "\n <tr><td>Model Name:</td><td><input type=\"text\" id=\"model_name\" name=\"model_name\" %s></td></tr>" +
                        "\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\" %s></td></tr>",
                (null == model_name) ? "" : "value=\"" + model_name + "\"",
                (null == model_version_string) ? "" : "value=\"" + model_version_string + "\""));
        if ((null != model_name) && (null != model_version_string)) {
            Integer model_version = Integer.parseInt(model_version_string);
            Model model = repository.findByModelNameAndModelVersion(model_name, model_version);
            if (null == model) {
                retString.append("\n<tr>Model not found</tr>");
                retString.append("\n</table>");
                retString.append("\n<input type=\"submit\" value=\"Get Model\">");
                retString.append("\n</form>");
            }
            else {
                retString.append("\n</table>");
                retString.append("\n<input type=\"submit\" value=\"Get Model\">");
                retString.append("\n</form>");
                retString.append("\n<hr>");

                retString.append("\n<form action=\"/web/score\">");
                retString.append(String.format("\n<table>" +
                                "\n <tr><td>Model Name:</td><td><input type=\"text\" id=\"model_name\" name=\"model_name\" value=\"%s\" readonly=\"true\"></td></tr>" +
                                "\n <tr><td>Model Version:</td><td><input type=\"text\" id=\"model_version\" name=\"model_version\"  value=\"%s\" readonly=\"true\"></td></tr>",
                        model_name, model_version));
                Evaluator evaluator = modelUtils.getEvaluator(model);
                List<ModelUtils.Vitals> inputs = modelUtils.listInputs(evaluator.getInputFields());
                retString.append("\n <tr><td>Model Inputs:</td><td><table>");
                for (ModelUtils.Vitals field : inputs) {
                    retString.append(String.format("<tr><td>%s:</td><td><input type=\"text\" id=\"%s\" name=\"%s\" value=\"%s\" required></td><td>%s</td><td>%s</td></tr>",
                            field.getName(), field.getName(), field.getName(), params.getOrDefault(field.getName(), ""), field.getOpType(), field.getDataType()));
                }
                retString.append("</table></td></tr>");

                // if no other params, then don't score
                if (!params.isEmpty()) {
                    // if some params, then show error
                    // if all params, then show score
                    List<ModelUtils.Vitals> missing = new ArrayList<ModelUtils.Vitals>();
                    for (ModelUtils.Vitals field : inputs) {
                        if (null == params.get(field.getName()))
                            missing.add(field);
                    }
                    if (missing.size() > 0) {
                        retString.append(String.format("<tr>Missing some fields: %s</tr>", String.join(", ", missing.stream().map(ModelUtils.Vitals::getName).collect(Collectors.toList()))));
                    } else {
                        Map<String,Object> score = modelUtils.applyModel(evaluator, params);
                        retString.append("\n<tr><td>Result:</td><td><table>");
                        score.forEach((k,v) -> retString.append(String.format("<tr><td>%s</td><td>%s</td></tr>", k, v.toString())));
                        retString.append("</table></td></tr>");
                    }
                }
                retString.append("</table>");
                retString.append("\n<input type=\"submit\" value=\"Score It\">");
                retString.append("\n</form>");
                retString.append("\n<hr>");


            }
        }
        else {
            retString.append("</table>");
            retString.append("\n<input type=\"submit\" value=\"Get Model\">");
            retString.append("\n</form>");
        }
        // endpoints
        retString.append("\n<hr>");
        retString.append(endpointList());
        retString.append("\n</body></html>");
        return retString.toString();
    }
}

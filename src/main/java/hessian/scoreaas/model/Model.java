package hessian.scoreaas.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.util.Objects;

@Entity
public class Model {
    @PartitionKey
    private String model_name;

    @ClusteringColumn(1)
    private Integer model_version;

    private String model;

    public Model() { }

    public Model(String model_name, Integer model_version, String model) {
        this.model_name = model_name;
        this.model_version = model_version;
        this.model = model;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public Integer getModel_version() {
        return model_version;
    }

    public void setModel_version(Integer model_version) {
        this.model_version = model_version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model)) return false;
        Model model1 = (Model) o;
        return Objects.equals(getModel_name(), model1.getModel_name()) &&
                Objects.equals(getModel_version(), model1.getModel_version()) &&
                Objects.equals(getModel(), model1.getModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModel_name(), getModel_version(), getModel());
    }

    @Override
    public String toString() {
        return "Model{" +
                "model_name='" + model_name + '\'' +
                ", model_version=" + model_version +
                ", model='" + model + '\'' +
                '}';
    }
}

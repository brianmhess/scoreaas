package hessian.scoreaas.model;

import java.util.Objects;

public class NameVersion {
    private String name;
    private Integer version;

    public NameVersion() { }
    public NameVersion(String name, Integer version ) {
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

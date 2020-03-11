package hessian.scoreaas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScoreaasConfiguration {
    @Value("${dse.keyspace}")
    private String keyspace;

    public String getKeyspaceName() {
        return keyspace;
    }

}

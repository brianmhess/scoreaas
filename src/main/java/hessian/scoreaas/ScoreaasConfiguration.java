package hessian.scoreaas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScoreaasConfiguration {
    @Value("${dse.contactPoints}")
    public String contactPoints;

    @Value("${dse.port}")
    private int port;

    @Value("${dse.keyspace}")
    private String keyspace;

    public String getContactPoints() {
        return contactPoints;
    }

    public String getKeyspaceName() {
        return keyspace;
    }

    public int getPort() {
        return port;
    }

}

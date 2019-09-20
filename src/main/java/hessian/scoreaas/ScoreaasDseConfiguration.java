package hessian.scoreaas;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.dse.driver.api.core.DseSessionBuilder;
import hessian.scoreaas.model.ModelDao;
import hessian.scoreaas.model.ModelMapper;
import hessian.scoreaas.model.ModelMapperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScoreaasDseConfiguration {
    @Value("${dse.contactPoints}")
    public String contactPoints;

    @Value("${dse.port}")
    private int port;

    @Value("${dse.keyspace}")
    private String keyspace;

    @Value("${dse.localDC}")
    private String localDatacenter;

    public String getContactPoints() {
        return contactPoints;
    }

    public int getPort() {
        return port;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String getLocalDatacenter() {
        return localDatacenter;
    }

    @Bean
    public DseSession dseSession() {
        DseSessionBuilder dseSessionBuilder = DseSession.builder().withLocalDatacenter(localDatacenter);
        for (String s : contactPoints.split(","))
            dseSessionBuilder.addContactPoint(InetSocketAddress.createUnresolved(s, port));
        return dseSessionBuilder.build();
    }

    @Bean
    public ModelMapper modelMapper(DseSession dseSession) {
        return new ModelMapperBuilder(dseSession).build();
    }

    @Bean
    public ModelDao modelDao(ModelMapper modelMapper) {
        return modelMapper.modelDao("scoreaas", "model");
    }

}

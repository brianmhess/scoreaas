package hessian.scoreaas;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.dse.driver.api.core.DseSessionBuilder;
import hessian.scoreaas.model.ModelDao;
import hessian.scoreaas.model.ModelMapper;
import hessian.scoreaas.model.ModelMapperBuilder;
import hessian.scoreaas.model.ModelUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScoreaasDseConfiguration {
    @Value("${dse.keyspace}")
    private String keyspace;

    @Value("${dse.localDC}")
    private String localDatacenter;

    @Value("${dse.username}")
    private String username;

    @Value("${dse.password}")
    private String password;

    @Value("${dse.credentialsFile}")
    private String credentialsFile;

    public String getKeyspace() {
        return keyspace;
    }

    public String getLocalDatacenter() {
        return localDatacenter;
    }

    @Bean
    public DseSession dseSession() {
        DseSessionBuilder dseSessionBuilder = DseSession.builder()
                //.withLocalDatacenter(localDatacenter)
                .withAuthCredentials(username, password)
                .withCloudSecureConnectBundle(this.getClass().getResourceAsStream(credentialsFile));
        return dseSessionBuilder.build();
    }

    @Bean
    public ModelMapper modelMapper(DseSession dseSession) {
        return new ModelMapperBuilder(dseSession).build();
    }

    @Bean
    public ModelDao modelDao(ModelMapper modelMapper) {
        return modelMapper.modelDao(keyspace, "model");
    }

    @Bean
    public ModelUtils modelUtils() { return new ModelUtils(); }
}

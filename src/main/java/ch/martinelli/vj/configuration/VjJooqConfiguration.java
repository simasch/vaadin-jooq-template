package ch.martinelli.vj.configuration;

import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VjJooqConfiguration {

    @Bean
    public DefaultConfigurationCustomizer configurationCustomizer() {
        // Enable optimistic locking
        return (DefaultConfiguration c) -> c.settings()
                .withExecuteWithOptimisticLocking(true);
    }
}

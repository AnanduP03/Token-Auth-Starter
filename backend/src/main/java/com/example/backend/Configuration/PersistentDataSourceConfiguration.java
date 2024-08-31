package com.example.backend.Configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PersistentDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.persistent")
    public DataSourceProperties persistentDataSourceProperties(){return new DataSourceProperties();}

    @Bean
    @Primary
    public DataSource persistentDataSource(){
        return persistentDataSourceProperties().initializeDataSourceBuilder().build();
    }
}

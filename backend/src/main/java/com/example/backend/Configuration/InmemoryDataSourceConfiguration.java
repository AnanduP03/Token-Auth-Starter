package com.example.backend.Configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class InmemoryDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.inmemory")
    public DataSourceProperties inmemoryDataSourceProperties(){return new DataSourceProperties();}

    @Bean
    public DataSource inmemoryDataSource(){
        return inmemoryDataSourceProperties().initializeDataSourceBuilder().build();
    }
}

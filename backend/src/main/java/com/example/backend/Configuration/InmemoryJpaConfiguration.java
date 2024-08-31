package com.example.backend.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.example.backend.Repository.inmemory"},
        entityManagerFactoryRef = "inmemoryEntityManagerFactory",
        transactionManagerRef = "inmemoryTransactionManager"
)
public class InmemoryJpaConfiguration {

    @Value("${spring.jpa.status}")
    public String springJpaStatus;


    @Bean
    public LocalContainerEntityManagerFactoryBean inmemoryEntityManagerFactory(
            @Qualifier("inmemoryDataSource")DataSource dataSource,
            EntityManagerFactoryBuilder builder
            ){
        Map<String,String> properties=new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",springJpaStatus);
        properties.put("properties.hibernate.dialect","org.hibernate.dialect.H2Dialect");

        return builder
                .dataSource(dataSource)
                .packages("com.example.backend.Model.inmemory")
                .properties(properties)
                .build();
    }

    @Bean
    public PlatformTransactionManager inmemoryTransactionManager(
            @Qualifier(
                    "inmemoryEntityManagerFactory"
            ) LocalContainerEntityManagerFactoryBean inmemoryEntityManagerFactory
    ){
        return new JpaTransactionManager(
                Objects.requireNonNull(inmemoryEntityManagerFactory.getObject())
        );
    }
}

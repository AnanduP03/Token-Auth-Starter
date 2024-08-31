package com.example.backend.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.example.backend.Repository.persistent",
        entityManagerFactoryRef = "persistentEntityManagerFactory",
        transactionManagerRef = "persistentTransactionManager"
)
public class PersistentJpaConfiguration {

    @Value("${spring.jpa.status}")
    public String springJpaStatus;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean persistentEntityManagerFactory(
            @Qualifier("persistentDataSource")DataSource dataSource,
            EntityManagerFactoryBuilder builder
            ){
        Map<String,String> properties=new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",springJpaStatus);
        properties.put("hibernate.dialect","org.hibernate.dialect.MySQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.example.backend.Model.persistent")
                .properties(properties)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager persistentTransactionManager(
            @Qualifier("persistentEntityManagerFactory") LocalContainerEntityManagerFactoryBean persistentEntityManagerFactory
    ){
        return new JpaTransactionManager(
                Objects.requireNonNull(persistentEntityManagerFactory.getObject())
        );
    }
}

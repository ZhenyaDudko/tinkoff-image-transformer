package com.wisestudent.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
public class DBConfig {
    private static volatile PostgreSQLContainer<?> postgreSQLContainer = null;


    private static PostgreSQLContainer<?> getPostgreSQLContainer(){
        PostgreSQLContainer<?> instance = postgreSQLContainer;
        if(isNull(instance)){
            synchronized (PostgreSQLContainer.class){
                postgreSQLContainer = instance =
                        new PostgreSQLContainer<>("postgres:14")
                                .withDatabaseName("public")
                                .withUsername("postgres")
                                .withPassword("postgres")
                                .withReuse(true)
                                .withLogConsumer(new Slf4jLogConsumer(log))
                                .withStartupTimeout(Duration.ofSeconds(60));
                postgreSQLContainer.start();
            }
        }
        return instance;
    }

    @Component("PostgresInitializer")
    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var postgreSQLContainer = getPostgreSQLContainer();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.liquibase.change_log=classpath:db/changelog/db.changelog-master.yaml",
                    "spring.datasource.driverClassName=org.hibernate.dialect.PostgreSQLDialect"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}

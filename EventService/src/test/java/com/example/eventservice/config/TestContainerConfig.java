package com.example.eventservice.config;

import jakarta.annotation.PostConstruct;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContainerConfig {

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3")
            .withExposedPorts(27017);

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @PostConstruct
    public void startContainers() {
        mongoDBContainer.start();
        postgreSQLContainer.start();
    }

    @Bean
    public String mongoUri() {
        return mongoDBContainer.getReplicaSetUrl();
    }

    @Bean
    public String postgresJdbcUrl() {
        return postgreSQLContainer.getJdbcUrl();
    }
}


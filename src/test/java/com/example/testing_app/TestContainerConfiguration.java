package com.example.testing_app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfiguration {
    @Bean
    @ServiceConnection
    public MySQLContainer<?> mysqlContainer() {
        MySQLContainer<?> container=new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));
        container.start();
        return container;
    }
}

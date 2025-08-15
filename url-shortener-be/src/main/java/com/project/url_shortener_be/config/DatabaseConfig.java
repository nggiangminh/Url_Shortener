package com.project.url_shortener_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.project.url_shortener_be.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration will be handled by Spring Boot auto-configuration
    // based on application.properties
}

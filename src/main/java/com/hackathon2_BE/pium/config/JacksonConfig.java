package com.hackathon2_BE.pium.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer javaTimeModule() {
        return builder -> builder
                .modulesToInstall(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
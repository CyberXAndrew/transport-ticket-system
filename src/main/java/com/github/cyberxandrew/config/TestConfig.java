package com.github.cyberxandrew.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class TestConfig {

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false); //Optional but recommended
        return objectMapper;
    }
}
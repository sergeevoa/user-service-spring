package com.example.userservice.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

@TestConfiguration
public class TestJacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {   //Настройка билдера объектов типа ObjectMapper
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(new JavaTimeModule());
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        builder.serializerByType(
                java.time.LocalDateTime.class,
                new LocalDateTimeSerializer(
                        //Устанавливаем явно паттерн форматирования LocalDateTime при извлечении его из json
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                )
        );
        return builder;
    }
}

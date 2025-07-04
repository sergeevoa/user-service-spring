package com.example.userservice.service;

import com.example.userservice.dto.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    //Аннотация означает, что мы подставляем в переменную значение из конфигурационного файла.
    @Value("${app.kafka.topic}")
    private String topic;

    public KafkaEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserEvent event) {
        kafkaTemplate.send(topic, event);           //Название топика - это ключ сообщения
    }
}

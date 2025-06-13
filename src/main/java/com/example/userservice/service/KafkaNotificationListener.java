package com.example.userservice.service;

import com.example.userservice.dto.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationListener {
    private final EmailService emailService;

    public KafkaNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${app.kafka.topics}", containerFactory = "kafkaListenerContainerFactory")
    public void onUserEvent(UserEvent event) {
        String text;
        if ("CREATED".equals(event.getOperation())) {
            text = "Здравствуйте! Ваш аккаунт на сайте «UserServiceSpring» был успешно создан.";
        } else {
            text = "Здравствуйте! Ваш аккаунт на сайте «UserServiceSpring» был удалён.";
        }
        emailService.send(event.getEmail(), "Уведомление от сайта", text);
    }
}


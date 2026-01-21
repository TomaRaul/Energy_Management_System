package com.ds.ems.services;

import com.ds.ems.config.RabbitMQConfig;
import com.ds.ems.dtos.NotificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //@Autowired
    //private ObjectMapper objectMapper;

    // Pentru conversie JSON -> Obiect
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Asculta coada RabbitMQ
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String messageJson) {
        try {
            System.out.println("ALERTA PRIMITA DIN RABBITMQ: " + messageJson);

            // Convertim JSON string în obiect Java (opțional, poți trimite direct string-ul)
            NotificationDTO notification = objectMapper.readValue(messageJson, NotificationDTO.class);

            // Trimitem pe WebSocket la toți clientii abonați la /topic/alerts
            // Mesajul ajunge instant în React
            messagingTemplate.convertAndSend("/topic/alerts", messageJson);

        } catch (Exception e) {
            System.err.println("Eroare la procesarea notificării: " + e.getMessage());
        }
    }
}

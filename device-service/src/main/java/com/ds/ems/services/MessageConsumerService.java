package com.ds.ems.services;

import com.ds.ems.dtos.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
public class MessageConsumerService {

    private final UserSyncService userSyncService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    public MessageConsumerService(UserSyncService userSyncService, ObjectMapper objectMapper) {
        this.userSyncService = userSyncService;
        this.objectMapper = objectMapper;
    }

    /**
     * Consumer pentru evenimente de sincronizare users
     */
    @RabbitListener(queues = "${rabbitmq.queue.user.sync:user_sync_queue}")
    public void receiveUserCreatedEvent(UserCreatedEvent event) {
        try {
            log.info("Received user sync message: {}", event);

            // Parse JSON -> UserCreatedEvent
            // UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);

            // Procesează evenimentul
            userSyncService.handleUserCreated(event);

        } catch (Exception e) {
            //log.error("Error processing user sync message: {}", event, e);
            throw new RuntimeException("Failed to process user sync message", e);
        }
    }
}

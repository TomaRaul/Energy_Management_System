package com.ds.ems.services;

import com.ds.ems.dtos.UserCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.sync:sync_exchange}")
    private String syncExchange;

    @Value("${rabbitmq.routing.key.user:user.created}")
    private String userRoutingKey;

    private static final Logger log = LoggerFactory.getLogger(EventPublisherService.class);

    // Constructor manual
    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreatedEvent(Long userId){ //String name, String email, String role) {
        try {
            UserCreatedEvent event = new UserCreatedEvent(userId);//, name, email, role);

            log.info("Publishing user created event for user: {}", userId);

            rabbitTemplate.convertAndSend(syncExchange, userRoutingKey, event);

            log.info("User created event published successfully");

        } catch (Exception e) {
            log.error("Failed to publish user created event", e);
            // Nu arunc excepție pentru a nu bloca crearea user-ului
        }
    }
}

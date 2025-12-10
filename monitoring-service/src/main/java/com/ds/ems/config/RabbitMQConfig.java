package com.ds.ems.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.device.data}")
    private String deviceDataQueue;

//    @Value("${rabbitmq.queue.sync}")
//    private String syncQueue;

    @Value("${rabbitmq.exchange.sync}")
    private String syncExchange;

    @Value("${rabbitmq.routing.key.device}")
    private String deviceRoutingKey;

    @Value("${rabbitmq.routing.key.user}")
    private String userRoutingKey;

    // Queue pentru date de la device simulator
    @Bean
    public Queue deviceDataQueue() {
        return new Queue(deviceDataQueue, true);
    }

    // Queue pentru sincronizare
//    @Bean
//    public Queue syncQueue() {
//        return new Queue(syncQueue, true);
//    }

    // Exchange pentru sincronizare
    @Bean
    public TopicExchange syncExchange() {
        return new TopicExchange(syncExchange);
    }

    // Binding pentru device events
//    @Bean
//    public Binding deviceBinding() {
//        return BindingBuilder
//                .bind(syncQueue())
//                .to(syncExchange())
//                .with(deviceRoutingKey);
//    }

    // Binding pentru user events
//    @Bean
//    public Binding userBinding() {
//        return BindingBuilder
//                .bind(syncQueue())
//                .to(syncExchange())
//                .with(userRoutingKey);
//    }

    @Bean
    public MessageConverter messageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}

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

    @Value("${rabbitmq.queue.user.sync:user_sync_queue}")
    private String userSyncQueue;

    @Value("${rabbitmq.exchange.sync:sync_exchange}")
    private String syncExchange;

    @Value("${rabbitmq.routing.key.user:user.created}")
    private String userRoutingKey;

    // Queue pentru sincronizare users
    @Bean
    public Queue userSyncQueue() {
        return new Queue(userSyncQueue, true);
    }

    // Exchange pentru sincronizare
    @Bean
    public TopicExchange syncExchange() {
        return new TopicExchange(syncExchange);
    }

    // Binding pentru user events
    @Bean
    public Binding userBinding() {
        return BindingBuilder
                .bind(userSyncQueue())
                .to(syncExchange())
                .with(userRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

package com.ds.ems.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Constante trebuie să fie IDENTICE cu cele din Monitoring Service)
    public static final String QUEUE_NAME = "notification_queue";               // Coada ta
    public static final String EXCHANGE_NAME = "check.consumption.exchange";    // Exchange-ul unde trimite Monitoring
    public static final String ROUTING_KEY = "alert.notification";              // Cheia de rutare

    // Definirea Cozii (O ai deja)
    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true); // true = durable
    }

    // Definirea Exchange-ului
    // Trebuie sa existe ca sa poti face legatura
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // BINDING
    // Asta spune: "Orice mesaj trimis pe 'check.consumption.exchange'
    // cu cheia 'alert.notification', copiaza in 'notification_queue'"
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY);
    }
}

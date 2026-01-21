package com.ds.ems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic" este prefixul pentru mesajele pe care le trimitem noi (Server -> Client)
        // Exemplu: /topic/alerts
        config.enableSimpleBroker("/topic", "/queue");

        // "/app" este prefixul pentru mesajele care vin de la client (Client -> Server)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // aici conectez React: http://localhost:8085/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permite conexiunea de oriunde (React)
                .withSockJS(); // Fallback dacă browserul nu știe WebSocket pur
    }
}
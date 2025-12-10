package com.ds.ems.config.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.ds.ems.config.model.DeviceMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {
    private final ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final String queueName;
    private final Gson gson;

    public RabbitMQProducer(String host, int port, String username,
                            String password, String queueName) {
        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
        this.queueName = queueName;
        this.gson = new Gson();
    }

    public void connect() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();

        // Declarare/Creeare queue
        channel.queueDeclare(queueName, true, false, false, null);

        System.out.println("Connected to RabbitMQ successfully!");
        System.out.println("Queue: " + queueName);
    }

    public void sendMessage(DeviceMessage message) throws IOException {
        String jsonMessage = gson.toJson(message);

        // Adauga content-type și delivery mode
        com.rabbitmq.client.AMQP.BasicProperties properties =
                new com.rabbitmq.client.AMQP.BasicProperties.Builder()
                        .contentType("application/json")  // ← FIX-ul principal!
                        .deliveryMode(2) // persistent message
                        .priority(0)     // default priority
                        .build();

        channel.basicPublish("", queueName, properties,
                jsonMessage.getBytes(StandardCharsets.UTF_8));

        System.out.println("Sent: " + jsonMessage);
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
        System.out.println("Connection closed.");
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen() &&
                channel != null && channel.isOpen();
    }
}

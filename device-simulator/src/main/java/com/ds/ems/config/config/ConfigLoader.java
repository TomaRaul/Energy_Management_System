package com.ds.ems.config.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader(String configFile) throws IOException {
        properties = new Properties();

        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Config file not found, using default properties from classpath");
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream(configFile)) {
                if (input != null) {
                    properties.load(input);
                } else {
                    throw new IOException("Unable to find config file: " + configFile);
                }
            }
        }
    }

    public String getRabbitMQHost() {
        return properties.getProperty("rabbitmq.host", "localhost");
    }

    public int getRabbitMQPort() {
        return Integer.parseInt(properties.getProperty("rabbitmq.port", "5672"));
    }

    public String getRabbitMQUsername() {
        return properties.getProperty("rabbitmq.username", "admin");
    }

    public String getRabbitMQPassword() {
        return properties.getProperty("rabbitmq.password", "admin");
    }

    public String getRabbitMQQueue() {
        return properties.getProperty("rabbitmq.queue", "device_data");
    }

    public Long getDeviceId() {
        return Long.parseLong(properties.getProperty("device.id", "1"));
    }

    public long getSimulationInterval() {
        // Interval în milisecunde (default: 600000 = 10 minute)
        return Long.parseLong(properties.getProperty("simulation.interval", "600000"));
    }
}

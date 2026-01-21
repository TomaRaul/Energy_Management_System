package com.ds.ems.config;

import com.ds.ems.config.config.ConfigLoader;
import com.ds.ems.config.model.DeviceMessage;
import com.ds.ems.config.service.EnergyConsumptionGenerator;
import com.ds.ems.config.service.RabbitMQProducer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DeviceSimulatorApp {
private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

private static volatile boolean running = true;

public static void main(String[] args) {
    System.out.println("===========================================");
    System.out.println("   Device Simulator - Energy Management   ");
    System.out.println("===========================================");

    ConfigLoader config;
    try {
        config = new ConfigLoader("C:\\UTCluj\\an4\\sem1\\SD\\ds2025_assig02\\ems\\device-simulator\\src\\main\\resources\\config.properties");
    } catch (IOException e) {
        System.err.println("Error loading configuration: " + e.getMessage());
        return;
    }

    RabbitMQProducer producer = new RabbitMQProducer(
            config.getRabbitMQHost(),
            config.getRabbitMQPort(),
            config.getRabbitMQUsername(),
            config.getRabbitMQPassword(),
            config.getRabbitMQQueue()
    );

    try {
        producer.connect();

        long deviceId = config.getDeviceId();
        long interval = config.getSimulationInterval();

        System.out.println("\nSimulation Configuration:");
        System.out.println("Device ID: " + deviceId);
        System.out.println("Interval: " + (interval / 1000) + " seconds");
        System.out.println("\nPress 'q' and ENTER to stop the simulation.\n");

        EnergyConsumptionGenerator generator = new EnergyConsumptionGenerator();

        // Thread pentru citirea comenzii de stop
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (input.equalsIgnoreCase("q")) {
                        running = false;
                        System.out.println("\nStopping simulation...");
                    }
                }
            }
            scanner.close();
        });
        inputThread.setDaemon(true);
        inputThread.start();

        int messageCount = 0;

        while (running) {
            try {
                // Genereaza consum
                double consumption = generator.generateConsumption();

                // Creeaza mesaj
                DeviceMessage message = new DeviceMessage(
                        LocalDateTime.now().plusHours(messageCount/10).format(DATE_FORMATTER),
                        deviceId,
                        consumption
                );

                // Trimite mesaj
                producer.sendMessage(message);
                messageCount++;

                // Periodic ajusteaza base load pentru variație
                if (messageCount % 10 == 0) {
                    generator.adjustBaseLoad();
                }

                // așteapta intervalul specificat
                Thread.sleep(interval);

            } catch (InterruptedException e) {
                System.out.println("Simulation interrupted.");
                running = false;
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
                // incearca sa reconecteze
                if (!producer.isConnected()) {
                    System.out.println("Attempting to reconnect...");
                    try {
                        producer.connect();
                    } catch (Exception ex) {
                        System.err.println("Reconnection failed: " + ex.getMessage());
                        running = false;
                    }
                }
            }
        }

        System.out.println("\nTotal messages sent: " + messageCount);

    } catch (IOException | TimeoutException e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            producer.close();
        } catch (IOException | TimeoutException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    System.out.println("Simulator stopped.");
}
}

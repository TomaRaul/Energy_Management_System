package com.ds.ems.services;

import com.ds.ems.dtos.DeviceMessage;
import com.ds.ems.dtos.NotificationDTO;
import com.ds.ems.entities.HourlyConsumption;
import com.ds.ems.repositories.HourlyConsumptionRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class ConsumptionAggregationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumptionAggregationService.class);
    private final HourlyConsumptionRepository hourlyConsumptionRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${websocket.service.url}")
    private String websocketUrl;
    @Value("${device.service.url}")
    private String deviceServiceUrl;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConsumptionAggregationService(HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

     // Proceseaza mesajul de la device și agrega consumul pe ora
    @Transactional
    public void processDeviceMessage(DeviceMessage message) {
        try {
            LocalDateTime timestamp = message.getTimestampAsDateTime();
            Long deviceId = message.getDeviceId();
            Double measurementValue = message.getMeasurementValue();

            // Rotunjeste timestamp-ul la inceputul orei
            LocalDateTime hourTimestamp = timestamp.truncatedTo(ChronoUnit.HOURS);

            LOGGER.debug("Processing measurement for device {} at hour {} with value {}",
                    deviceId, hourTimestamp, measurementValue);

            // Cauta inregistrarea pentru ora respectiva
            HourlyConsumption hourlyConsumption = hourlyConsumptionRepository
                    .findByDeviceIdAndHourTimestamp(deviceId, hourTimestamp)
                    .orElse(new HourlyConsumption());

            // Daca e noua se realizeaza initializerea
            if (hourlyConsumption.getId() == null) {
                hourlyConsumption.setDeviceId(deviceId);
                hourlyConsumption.setHourTimestamp(hourTimestamp);
                hourlyConsumption.setHourConsumption(0.0);
                //hourlyConsumption.setMeasurementCount(0);
                LOGGER.info("Creating new hourly consumption record for device {} at hour {}",
                        deviceId, hourTimestamp);
            }

            // Adauga valoarea la total
            Double newTotalConsumption = hourlyConsumption.getHourConsumption() + measurementValue;
            hourlyConsumption.setHourConsumption(
                    hourlyConsumption.getHourConsumption() + measurementValue
            );

            // Salveaza în baza de date
            hourlyConsumptionRepository.save(hourlyConsumption);

            LOGGER.info("Updated hourly consumption for device {} at hour {}: Total={} kWh",
                    deviceId, hourTimestamp, hourlyConsumption.getHourConsumption());


            // Verificare supraconsum (Overconsumption Alert)
            checkAndSendAlert(deviceId, newTotalConsumption, hourTimestamp);

        } catch (Exception e) {
            LOGGER.error("Error processing device message", e);
            throw e;
        }
    }

    private void checkAndSendAlert(Long deviceId, Double currentConsumption, LocalDateTime alertTime) {
        String exchange = "check.consumption.exchange";
        String routingKey = "alert.notification";

        try {
                double maxLimit = getMaxHourlyEnergyConsumption(deviceId);

                if (currentConsumption > maxLimit) {
                    LOGGER.warn("ALERT: Device {} exceeded limit! Current: {}, Max: {}", deviceId, currentConsumption, maxLimit);

                    NotificationDTO alert = new NotificationDTO();
                    alert.setDeviceId(deviceId.toString());
                    alert.setMessage("Device-ul '" + deviceId + "' a depasit limita! Consum: "
                                     + String.format("%.2f", currentConsumption) + " kWh. Data si ora: " + alertTime.toString() );

                    // trimitem direct prin WebSocket
                    //restTemplate.postForObject(websocketUrl, alert, Void.class);

                    // trimitem pe Rabbit --> monitoring nu trebuie sa știe numele cozii suficient doar Exchange si RoutingKey
                    rabbitTemplate.convertAndSend(exchange, routingKey, alert);
                }
        } catch (Exception e) {
            LOGGER.error("Nu am putut verifica limita de consum pentru device " + deviceId, e);
        }
    }
    private double getMaxHourlyEnergyConsumption(Long deviceId) {
        double maxLimit = 0.0;
        String url = deviceServiceUrl + "/" + deviceId;
        try {
            String strResponse = restTemplate.getForObject(url, String.class);
            JsonNode deviceJson = objectMapper.readTree(strResponse);
            if (deviceJson != null) {
                maxLimit = deviceJson.path("MCV").asDouble();
            }
        } catch (Exception e) {
            LOGGER.error("Error processing device message", e);
        }
        return maxLimit;
    }
}

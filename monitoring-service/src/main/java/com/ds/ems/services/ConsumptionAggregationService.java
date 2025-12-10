package com.ds.ems.services;

import com.ds.ems.dtos.DeviceMessage;
import com.ds.ems.entities.HourlyConsumption;
import com.ds.ems.repositories.HourlyConsumptionRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ConsumptionAggregationService(HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }


     // Proceseaza mesajul de la device și agrega consumul pe oră
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
            hourlyConsumption.setHourConsumption(
                    hourlyConsumption.getHourConsumption() + measurementValue
            );

            // Salveaza în baza de date
            hourlyConsumptionRepository.save(hourlyConsumption);

            LOGGER.info("Updated hourly consumption for device {} at hour {}: Total={} kWh",
                    deviceId, hourTimestamp, hourlyConsumption.getHourConsumption());

        } catch (Exception e) {
            LOGGER.error("Error processing device message", e);
            throw e;
        }
    }
}

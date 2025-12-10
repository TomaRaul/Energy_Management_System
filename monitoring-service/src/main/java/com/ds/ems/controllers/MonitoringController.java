package com.ds.ems.controllers;

import com.ds.ems.dtos.ConsumptionDTO;
//import com.ds.ems.entities.DeviceInfo;
import com.ds.ems.entities.HourlyConsumption;
//import com.ds.ems.repositories.DeviceInfoRepository;
import com.ds.ems.repositories.HourlyConsumptionRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/monitor")
//@RequiredArgsConstructor
//@Slf4j
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "http://localhost:3000")
public class MonitoringController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringController.class);
    private final HourlyConsumptionRepository hourlyConsumptionRepository;
    //private final DeviceInfoRepository deviceInfoRepository;

    public MonitoringController(HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    /**
     * Obține consumul orare pentru un device într-o anumită zi
     * GET /monitor/consumption?deviceId=1&date=2025-01-15
     */
    @GetMapping("/consumption")
    public ResponseEntity<List<ConsumptionDTO>> getHourlyConsumption(
            @RequestParam Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LOGGER.info("Fetching hourly consumption for device {} on date {}", deviceId, date);

        List<HourlyConsumption> consumptions = hourlyConsumptionRepository
                .findByDeviceIdAndDate(deviceId, date);

        List<ConsumptionDTO> result = consumptions.stream()
                .map(c -> new ConsumptionDTO(
                        c.getHourTimestamp().getHour(),
                        c.getHourConsumption()
                ))
                .collect(Collectors.toList());

        LOGGER.info("Found {} hourly records for device {}", result.size(), deviceId);

        return ResponseEntity.ok(result);
    }

    /**
     * Obține toate înregistrările pentru un device
     */
    @GetMapping("/consumption/{deviceId}/all")
    public ResponseEntity<List<HourlyConsumption>> getAllConsumptionForDevice(
            @PathVariable Long deviceId) {

        LOGGER.info("Fetching all consumption data for device {}", deviceId);

        List<HourlyConsumption> consumptions = hourlyConsumptionRepository
                .findByDeviceIdOrderByHourTimestampDesc(deviceId);

        return ResponseEntity.ok(consumptions);
    }

//    /**
//     * Obține toate device-urile sincronizate
//     */
//    @GetMapping("/devices")
//    public ResponseEntity<List<DeviceInfo>> getAllDevices() {
//        log.info("Fetching all synchronized devices");
//        return ResponseEntity.ok(deviceInfoRepository.findAll());
//    }
//
//    /**
//     * Obține device-urile pentru un user
//     */
//    @GetMapping("/devices/user/{userId}")
//    public ResponseEntity<List<DeviceInfo>> getDevicesByUser(@PathVariable Long userId) {
//        log.info("Fetching devices for user {}", userId);
//        return ResponseEntity.ok(deviceInfoRepository.findByUserId(userId));
//    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Monitoring Service is running");
    }
}

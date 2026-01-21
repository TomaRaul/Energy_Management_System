package com.ds.ems.services;

//import com.ds.ems.dtos.DeviceCreatedEvent;
import com.ds.ems.dtos.DeviceMessage;
//import com.ds.ems.dtos.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class MessageConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerService.class);
    private final ConsumptionAggregationService aggregationService;
    //private final SynchronizationService synchronizationService;
    private final ObjectMapper objectMapper;

    public MessageConsumerService(ConsumptionAggregationService aggregationService, ObjectMapper objectMapper) {
        this.aggregationService = aggregationService;
        this.objectMapper = objectMapper;
    }

    /**
     * Consumer pentru mesaje de la Device Simulator
     */
    @RabbitListener(queues = "${rabbitmq.queue.device.data}")
    public void receiveDeviceData(DeviceMessage deviceMessage) {
        try {
            //LOGGER.info("Received device data message: {}", message);

            //DeviceMessage deviceMessage = objectMapper.readValue(message, DeviceMessage.class);

            LOGGER.debug("Parsed message - Device ID: {}, Value: {}, Timestamp: {}",
                    deviceMessage.getDeviceId(),
                    deviceMessage.getMeasurementValue(),
                    deviceMessage.getTimestamp());

            aggregationService.processDeviceMessage(deviceMessage);

        } catch (Exception e) {
           // LOGGER.error("Error processing device data message: {}", message, e);
        }
    }
}

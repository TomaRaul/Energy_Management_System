package com.ds.ems.controllers;

import com.ds.ems.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Monitoring Service va apela acest endpoint cand cineva depașește consumul
    // POST http://localhost:8086/api/send-notification
    @PostMapping("/api/send-notification")
    public void sendAlert(@RequestBody NotificationDTO notification) {
        System.out.println("ALERTA PRIMITA: " + notification.getMessage() + " pentru device: " + notification.getDeviceId());

        // mesajul ajunge la TOȚI, in frontend filtrez pt cine e
        messagingTemplate.convertAndSend("/topic/alerts", notification);
    }
}

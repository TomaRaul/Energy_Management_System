package com.ds.ems.controllers;

import com.ds.ems.dtos.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // WebSocket direct (React -> WebSocket -> React) ---
    // cand un client trimite la "/app/chat", mesajul e retrimis la "/topic/chat"
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessageDTO broadcastMessage(@Payload ChatMessageDTO message) {
        return message;
    }

    @PostMapping("/api/send-chat")
    public void sendChatMessageFromBackend(@RequestBody ChatMessageDTO message) {

        System.out.println("Mesaj primit pe REST (8086): " + message.getContent());

        if ("ADMIN_Broadcast".equals(message.getReceiverId())) {
            // catre Admin
            messagingTemplate.convertAndSend("/topic/admin", message);
        } else if (message.getReceiverId() != null) {
            // raspuns privat catre un singur user (Cazul Admin -> Client)
            messagingTemplate.convertAndSend("/topic/chat", message);
        } else {
            // Broadcast general (fallback)
            messagingTemplate.convertAndSend("/topic/chat", message);
        }
    }
}

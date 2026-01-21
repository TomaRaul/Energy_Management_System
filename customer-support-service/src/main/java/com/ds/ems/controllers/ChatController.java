package com.ds.ems.controllers;

import com.ds.ems.dtos.ChatMessageDTO;
import com.ds.ems.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*") // permite apeluri din React
public class ChatController {

    @Autowired
    private ChatService chatService;

    // React trimite aici: POST http://localhost:8087/chat/send
    // Body: { "sender": "tomarm", "content": "hello" }
    @PostMapping("/send")
    public String receiveMessage(@RequestBody ChatMessageDTO message) {
        //  procesare mesaj
        System.out.println("Mesaj primit de la user: " + message.getContent());

        // Logica de reguli + AI
        chatService.processMessage(message);

        return "Message received for processing";
    }
}

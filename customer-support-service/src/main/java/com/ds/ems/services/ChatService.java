package com.ds.ems.services;

import com.ds.ems.dtos.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GeminiService geminiService; // serviciul Gemini

    @Value("${websocket.service.url}")
    private String websocketUrl;

    // regulile (Mapare intrebare -> raspuns)
    private final Map<String, String> rules = new HashMap<>();

    public ChatService() {
        initializeRules();
    }

    //  Rule-based Customer Support 10 reguli
    private void initializeRules() {
        rules.put("hello", "Hello! How can I help you today with your energy consumption?");
        rules.put("salut", "Salut! Cu ce te pot ajuta?");
        rules.put("invoice", "Invoices are generated on the 1st of each month.");
        rules.put("factura", "Facturile se emit pe data de 1 a fiecarei luni.");
        rules.put("device", "You can verify your devices in the 'My Devices' tab.");
        rules.put("dispozitiv", "Poti vedea dispozitivele tale în tab-ul 'My Devices'.");
        rules.put("error", "If you see an error, please try restarting the application.");
        rules.put("eroare", "Daca intampini o eroare, te rugam sa restartezi aplicatia.");
        rules.put("contact", "You can contact human support at +40 777 888 999.");
        rules.put("limit", "If you exceed the limit, you will receive a notification immediately.");
        rules.put("admin", "I am notifying an administrator right now.");
    }

    public void processMessage(ChatMessageDTO incomingMessage) {
        String userText = incomingMessage.getContent().toLowerCase();
        String responseText = null;

        // DETECTARE CERERE EXPLICITA ADMIN
        if (userText.contains("admin") ||
                userText.contains("om") ||
                userText.contains("human") ||
                userText.contains("ajutor") ||
                userText.contains("support")) {

            // anuntam utilizatorul ca se face legatura
            sendResponseToUser(incomingMessage.getSender(), "Am înțeles. Redirecționez conversația către un administrator. Te rog așteaptă...");

            // trimitem mesajul original catre Coada Adminilor
            forwardToAdminDashboard(incomingMessage);
            return; // STOP: Nu mai intreb si AI-ul
        }

        // verif daca mesajul se potriveste cu o REGULA
        for (Map.Entry<String, String> entry : rules.entrySet()) {
            if (userText.contains(entry.getKey())) {
                responseText = entry.getValue();
                break;
            }
        }

        // daca nu am gasit regula -> AI DRIVEN
        if (responseText == null) {
            responseText = callAIModel(incomingMessage.getContent());

            // VALIDARE: Dacă AI-ul da un raspuns de eroare sau "I don't know", escaladam la Admin
            if (responseText.contains("offline") || responseText.contains("error")) {
                sendResponseToUser(incomingMessage.getSender(), "Întâmpin dificultăți tehnice. Un admin te va prelua curând.");
                forwardToAdminDashboard(incomingMessage);
                return;
            }
        }

        // trimitem raspunsul inapoi la Utilizator (prin WebSocket Microservice)
        //sendResponseToWebSocket(incomingMessage.getSender(), responseText);
        sendResponseToUser(incomingMessage.getSender(), responseText);
    }

    // Trimite mesaj inapoi la utilizatorul care a întrebat (Chat-ul normal)
    private void sendResponseToUser(String receiverUser, String content) {
        ChatMessageDTO response = new ChatMessageDTO();
        response.setSender("SupportBot");
        response.setContent(content);
        response.setReceiverId(receiverUser); // raspundem userului specific

        try {
            restTemplate.postForObject(websocketUrl, response, Void.class);
        } catch (Exception e) {
            System.err.println("Eroare trimitere la user: " + e.getMessage());
        }
    }

    // LOGICA DE ESCALADARE CATRE ADMIN
    private void forwardToAdminDashboard(ChatMessageDTO originalMessage) {
        // construim un mesaj special pentru Admin
        ChatMessageDTO adminMessage = new ChatMessageDTO();

        // pastrez sender-ul original ca Adminul să știe CINE a întrebat
        adminMessage.setSender(originalMessage.getSender());

        // Conținutul mesajului
        adminMessage.setContent("[REQUEST ADMIN] " + originalMessage.getContent());

        // Setam destinatarul ca fiind "admin" (sau un ID specific de admin daca ai unul)
        // WebSocket Service trebuie sa știe să trimita asta pe canalul /topic/admin
        adminMessage.setReceiverId("ADMIN_Broadcast");

        try {
            restTemplate.postForObject(websocketUrl, adminMessage, Void.class);
            System.out.println("Mesaj escaladat la Admin: " + originalMessage.getContent());
        } catch (Exception e) {
            System.err.println("Eroare escaladare admin: " + e.getMessage());
        }
    }

    // AI Driven
    private String callAIModel(String question) {
        System.out.println("Nu am gasit regula. Intreb AI-ul...");
        String responseText = geminiService.getAnswer(question);

        return responseText;
    }

    private void sendResponseToWebSocket(String receiverUser, String content) {
        // construim mesajul de raspuns
        ChatMessageDTO response = new ChatMessageDTO();
        response.setSender("SupportBot");
        response.setContent(content);
        response.setReceiverId(receiverUser); // Raspundem userului care a intrebat

        // Websocket Microservice
        try {
            restTemplate.postForObject(websocketUrl, response, Void.class);
            System.out.println("Raspuns trimis la Websocket MS: " + content);
        } catch (Exception e) {
            System.err.println("Eroare la conectarea cu WebSocket MS: " + e.getMessage());
        }
    }
}

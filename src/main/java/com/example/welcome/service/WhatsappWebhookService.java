package com.example.welcome.service;

import com.example.welcome.entity.IncomingMessage;
import com.example.welcome.repository.IncomingMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WhatsappWebhookService {

    @Autowired
    private WhatsappSendMessageService whatsappSendMessageService;

    @Autowired
    private IncomingMessageRepository incomingMessageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> userStates = new HashMap<>();

    public String processWebhookMessage(String messageJson) {
        try {
            log.info("Incoming message: {}", messageJson);
            JsonNode root = objectMapper.readTree(messageJson);
            JsonNode messagesNode = root.path("entry").get(0).path("changes").get(0).path("value").path("messages");

            if (messagesNode.isArray() && messagesNode.size() > 0) {
                JsonNode messageNode = messagesNode.get(0);
                String from = messageNode.path("from").asText();
                String messageId = messageNode.path("id").asText();
                long timestampMillis = messageNode.path("timestamp").asLong() * 1000;
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault());
                String messageType = messageNode.path("type").asText();

                String messageContent = "";
                if (messageType.equals("text")) {
                    messageContent = messageNode.path("text").path("body").asText();
                } else if (messageType.equals("interactive")) {
                    String interactiveType = messageNode.path("interactive").path("type").asText();
                    if (interactiveType.equals("button_reply")) {
                        messageContent = messageNode.path("interactive").path("button_reply").path("id").asText();
                    }
                }

                // Save incoming message to the database
                IncomingMessage incomingMessage = new IncomingMessage(null, messageId, from, timestamp, messageType, messageContent);
                incomingMessageRepository.save(incomingMessage);

                if ("text".equals(messageType)) {
                    userStates.put(from, "WELCOME");
                    whatsappSendMessageService.sendWelcomeMessage(from);
                } else if ("interactive".equals(messageType)) {
                    handleInteractiveMessage(from, messageContent);
                }
            }
        } catch (Exception e) {
            log.error("Error processing webhook message", e);
            return "Error processing message";
        }
        return "Message received successfully";
    }

    private void handleInteractiveMessage(String from, String buttonId) {
        if ("HOSPITAL_INFO".equals(buttonId)) {
            userStates.put(from, "HOSPITAL_INFO");
            whatsappSendMessageService.sendHospitalInfoMessage(from);
            whatsappSendMessageService.sendGoBackEndMessage(from);
        } else if ("HOSPITAL_WARD".equals(buttonId)) {
            userStates.put(from, "HOSPITAL_WARD");
            whatsappSendMessageService.sendHospitalWardMessage(from);
            whatsappSendMessageService.sendGoBackEndMessage(from);
        } else if ("GO_BACK".equals(buttonId)) {
            handleGoBack(from);
        } else if ("END".equals(buttonId)) {
            handleEndConversation(from);
        }
    }

    private void handleGoBack(String from) {
        String state = userStates.get(from);
        if ("HOSPITAL_INFO".equals(state) || "HOSPITAL_WARD".equals(state)) {
            whatsappSendMessageService.sendWelcomeMessage(from);
        }
    }

    private void handleEndConversation(String from) {
        log.info("Ending conversation with user: {}", from);
        userStates.remove(from);

    }
}

package com.example.welcome.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class WhatsappSendMessageService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendWelcomeMessage(String to) {
        sendMessageFromFile("welcomeMessage.json", to);
    }

    public void sendGoBackEndMessage(String to) {
        sendMessageFromFile("goBackEndMessage.json", to);
    }

    public void sendHospitalInfoMessage(String to) {
        String hospitalInfo = "Welcome to XYZ Hospital! Our hospital is equipped with state-of-the-art facilities and experienced medical staff to provide you with the best healthcare services. We offer a wide range of medical services including:\n" +
                "- General Medicine\n" +
                "- Surgery\n" +
                "- Pediatrics\n" +
                "- Orthopedics\n" +
                "- Cardiology\n" +
                "- Neurology\n" +
                "- Gynecology\n" +
                "For more information, visit our website or contact our support team.";
        sendMessage(createTextMessage(to, hospitalInfo));
    }

    public void sendHospitalWardMessage(String to) {
        String hospitalWardsInfo = "Our hospital has several specialized wards to cater to the needs of our patients:\n" +
                "- Emergency Ward: 24/7 emergency services\n" +
                "- Intensive Care Unit (ICU): For critical care\n" +
                "- Maternity Ward: For expectant mothers and newborns\n" +
                "- Pediatric Ward: For children and adolescents\n" +
                "- Surgical Ward: For post-operative care\n" +
                "- General Ward: For general medical care\n" +
                "Our team of experienced doctors and nurses are dedicated to providing the best care in each ward.";
        sendMessage(createTextMessage(to, hospitalWardsInfo));
    }

    private void sendMessageFromFile(String fileName, String to) {
        try {
            String messageJson = new String(Files.readAllBytes(Paths.get(new ClassPathResource(fileName).getURI())));
            if (to != null) {
                messageJson = messageJson.replace("<recipient-phone-number>", to);
            }
            sendMessage(messageJson);
        } catch (Exception e) {
            log.error("Error sending message from file: {}", fileName, e);
        }
    }

    private void sendMessage(String messageJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            HttpEntity<String> request = new HttpEntity<>(messageJson, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            log.info("Response: {}", responseEntity.getBody());
        } catch (Exception e) {
            log.error("Error sending message", e);
        }
    }

    private String createTextMessage(String to, String text) {
        try {
            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("messaging_product", "whatsapp");
            messageNode.put("to", to);
            messageNode.put("type", "text");
            ObjectNode textNode = objectMapper.createObjectNode();
            textNode.put("body", text);
            messageNode.set("text", textNode);
            return messageNode.toString();
        } catch (Exception e) {
            log.error("Error creating text message", e);
            return null;
        }
    }
}

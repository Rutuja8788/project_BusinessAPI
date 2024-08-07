package com.example.welcome.controller;

import com.example.welcome.service.WhatsappSendMessageService;
import com.example.welcome.service.WhatsappWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class WhatsappController {

    @Value("${whatsapp.mode}")
    private String whatsappMode;

    @Value("${whatsapp.verification_token}")
    private String whatsappVerificationToken;

    @Autowired
    private WhatsappWebhookService whatsappWebhookService;

    @Autowired
    private WhatsappSendMessageService whatsappSendMessageService;

    @GetMapping("/webhook")
    public ResponseEntity<String> webhookVerify(@RequestParam("hub.mode") String mode,
                                                @RequestParam("hub.challenge") String challenge,
                                                @RequestParam("hub.verify_token") String token) {
        if (mode.equals(whatsappMode) && token.equals(whatsappVerificationToken)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(403).body("Verification token or mode mismatch");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveMessage(@RequestBody String messageJson)
    {
        String response = whatsappWebhookService.processWebhookMessage(messageJson);
        return ResponseEntity.ok(response);
    }
}

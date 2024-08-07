package com.example.welcome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappResponseDTO {
    private String id;
    private String status;
    private String timestamp;
    private String body;
}

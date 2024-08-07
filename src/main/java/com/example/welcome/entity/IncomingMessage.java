package com.example.welcome.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "incoming_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "from_phone", nullable = false)
    private String fromPhone;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "message_content", nullable = false)
    private String messageContent;
}

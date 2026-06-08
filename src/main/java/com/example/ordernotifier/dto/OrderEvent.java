package com.example.ordernotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long auditId;
    private String trackingNumber;
    private String recipientEmail;
    private String recipientCountryCode;
    private String senderCountryCode;
    private Integer statusCode;
    private Instant receivedAt;
}
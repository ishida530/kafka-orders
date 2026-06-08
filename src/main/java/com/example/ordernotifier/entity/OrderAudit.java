package com.example.ordernotifier.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "order_audit")
@Data
@NoArgsConstructor
public class OrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false, length = 3)
    private String recipientCountryCode;

    @Column(nullable = false, length = 3)
    private String senderCountryCode;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus processingStatus = ProcessingStatus.RECEIVED;

    public enum ProcessingStatus {
        RECEIVED, QUEUED, NOTIFIED, FAILED
    }

    public OrderAudit(String trackingNumber, String recipientEmail,
                      String recipientCountryCode, String senderCountryCode,
                      Integer statusCode) {
        this.trackingNumber = trackingNumber;
        this.recipientEmail = recipientEmail;
        this.recipientCountryCode = recipientCountryCode;
        this.senderCountryCode = senderCountryCode;
        this.statusCode = statusCode;
        this.receivedAt = Instant.now();
    }
}
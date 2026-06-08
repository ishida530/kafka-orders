package com.example.ordernotifier.service;

import com.example.ordernotifier.dto.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService {

    public void sendNotification(OrderEvent event) {

        log.info("========== EMAIL MOCK ==========");
        log.info("TO:      {}", event.getRecipientEmail());
        log.info("SUBJECT: Order update - {}", event.getTrackingNumber());
        log.info("BODY:");
        log.info("  Tracking number:    {}", event.getTrackingNumber());
        log.info("  Sender country:     {}", event.getSenderCountryCode());
        log.info("  Recipient country:  {}", event.getRecipientCountryCode());
        log.info("  Status code:        {}", event.getStatusCode());
        log.info("  Received at:        {}", event.getReceivedAt());
        log.info("================================");
    }
}
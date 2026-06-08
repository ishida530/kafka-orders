package com.example.ordernotifier.kafka;

import com.example.ordernotifier.dto.OrderEvent;
import com.example.ordernotifier.entity.OrderAudit;
import com.example.ordernotifier.repository.OrderAuditRepository;
import com.example.ordernotifier.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailNotificationService emailNotificationService;
    private final OrderAuditRepository orderAuditRepository;

    @KafkaListener(topics = "order-events", groupId = "order-notifier-group")
    public void consume(OrderEvent event) {
        log.info("Processing event from Kafka: tracking={}", event.getTrackingNumber());

        try {
            emailNotificationService.sendNotification(event);

            orderAuditRepository.findById(event.getAuditId()).ifPresent(audit -> {
                audit.setProcessingStatus(OrderAudit.ProcessingStatus.NOTIFIED);
                orderAuditRepository.save(audit);
            });

        } catch (Exception e) {
            log.error("Failed to process event: {}", event.getTrackingNumber(), e);
            orderAuditRepository.findById(event.getAuditId()).ifPresent(audit -> {
                audit.setProcessingStatus(OrderAudit.ProcessingStatus.FAILED);
                orderAuditRepository.save(audit);
            });
        }
    }
}
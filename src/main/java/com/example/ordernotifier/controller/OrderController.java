package com.example.ordernotifier.controller;

import com.example.ordernotifier.dto.OrderEvent;
import com.example.ordernotifier.dto.OrderRequest;
import com.example.ordernotifier.entity.OrderAudit;
import com.example.ordernotifier.kafka.OrderEventProducer;
import com.example.ordernotifier.repository.OrderAuditRepository;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderAuditRepository orderAuditRepository;
    private final OrderEventProducer orderEventProducer;

    @Qualifier("apiBucket")
    private final Bucket apiBucket;

    @PostMapping
    public ResponseEntity<String> receiveOrder(@Valid @RequestBody OrderRequest request) {
        OrderAudit audit = new OrderAudit(
                request.getTrackingNumber(),
                request.getRecipientEmail(),
                request.getRecipientCountryCode(),
                request.getSenderCountryCode(),
                request.getStatusCode()
        );
        orderAuditRepository.save(audit);

        if (!apiBucket.tryConsume(1)) {
            log.warn("API rate limit exceeded");
            audit.setProcessingStatus(OrderAudit.ProcessingStatus.REJECTED);
            orderAuditRepository.save(audit);
            return ResponseEntity.status(429).body("Too many requests - please slow down");
        }

        OrderEvent event = new OrderEvent(
                audit.getId(),
                audit.getTrackingNumber(),
                audit.getRecipientEmail(),
                audit.getRecipientCountryCode(),
                audit.getSenderCountryCode(),
                audit.getStatusCode(),
                audit.getReceivedAt()
        );
        orderEventProducer.send(event);

        return ResponseEntity.accepted().body("Order accepted");
    }


}
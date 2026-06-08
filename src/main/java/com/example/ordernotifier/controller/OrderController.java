package com.example.ordernotifier.controller;

import com.example.ordernotifier.dto.OrderRequest;
import com.example.ordernotifier.entity.OrderAudit;
import com.example.ordernotifier.repository.OrderAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderAuditRepository orderAuditRepository;

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
        log.info("Order received and saved: {}", request.getTrackingNumber());

        return ResponseEntity.accepted().body("Order accepted");
    }
}
package com.example.ordernotifier.kafka;

import com.example.ordernotifier.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void send(OrderEvent event) {
        kafkaTemplate.send(TOPIC, event.getTrackingNumber(), event);
        log.info("Event sent to Kafka: {}", event.getTrackingNumber());
    }
}
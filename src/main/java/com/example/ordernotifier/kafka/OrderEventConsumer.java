package com.example.ordernotifier.kafka;

import com.example.ordernotifier.dto.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "order-events", groupId = "order-notifier-group")
    public void consume(OrderEvent event) {
        log.info("Processing event from Kafka: tracking={}, email={}, status={}",
                event.getTrackingNumber(),
                event.getRecipientEmail(),
                event.getStatusCode());
//        TODO: Implement email notification logic here
    }
}
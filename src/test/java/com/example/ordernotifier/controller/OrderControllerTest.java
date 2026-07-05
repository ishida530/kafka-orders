package com.example.ordernotifier.controller;

import com.example.ordernotifier.dto.OrderRequest;
import com.example.ordernotifier.kafka.OrderEventProducer;
import com.example.ordernotifier.repository.OrderAuditRepository;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderAuditRepository orderAuditRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private Bucket apiBucket;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest sampleRequest() {
        OrderRequest request = new OrderRequest();
        request.setTrackingNumber("TRK-123");
        request.setRecipientEmail("test@example.com");
        request.setRecipientCountryCode("PL");
        request.setSenderCountryCode("DE");
        request.setStatusCode(10);
        return request;
    }

    @Test
    void shouldSaveAuditAsRejectedWhenRateLimitExceeded() {
        when(apiBucket.tryConsume(1)).thenReturn(false);

        OrderRequest sampleRequest = sampleRequest();
        ResponseEntity<String> response = orderController.receiveOrder(sampleRequest);

        assertEquals(429, response.getStatusCode().value());


        verify(orderAuditRepository, times(2)).save(any());

        verify(orderEventProducer, never()).send(any());
    }

    @Test
    void shouldAcceptAndForwardToKafkaWhenWithinRateLimit() {
        when(apiBucket.tryConsume(1)).thenReturn(true);

        OrderRequest sampleRequest = sampleRequest();
        ResponseEntity<String> response = orderController.receiveOrder(sampleRequest);

        assertEquals(202, response.getStatusCode().value());

        verify(orderAuditRepository, times(1)).save(any());

        verify(orderEventProducer, times(1)).send(any());
    }
}

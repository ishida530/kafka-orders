package com.example.ordernotifier.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    // Limit na API: max 100 requestów na sekundę
    @Bean(name = "apiBucket")
    public Bucket apiBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofSeconds(60))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Limit na emaile: max 10 na sekundę
    @Bean(name = "emailBucket")
    public Bucket emailBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofSeconds(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
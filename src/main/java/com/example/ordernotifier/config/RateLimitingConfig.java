package com.example.ordernotifier.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    @Value("${app.rate-limit.api.capacity}")
    private int apiCapacity;
    @Value("${app.rate-limit.api.refill-tokens}")
    private int apiRefillTokens;
    @Value("${app.rate-limit.api.refill-duration-seconds}")
    private int apiRefillDurationSeconds;

    @Value("${app.rate-limit.email.capacity}")
    private int emailCapacity;
    @Value("${app.rate-limit.email.refill-tokens}")
    private int emailRefillTokens;
    @Value("${app.rate-limit.email.refill-duration-seconds}")
    private int emailRefillDurationSeconds;

    @Bean(name = "apiBucket")
    public Bucket apiBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(apiCapacity)
                .refillGreedy(apiRefillTokens, Duration.ofSeconds(apiRefillDurationSeconds))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Bean(name = "emailBucket")
    public Bucket emailBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(emailCapacity)
                .refillGreedy(emailRefillTokens, Duration.ofSeconds(emailRefillDurationSeconds))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}

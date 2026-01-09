package com.rstracker.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * API Rate Limiting 설정
 * 기본적으로 분당 100회 요청 제한
 */
@Configuration
public class RateLimitConfig {
    
    @Bean(name = "apiRateLimitBucket")
    public Bucket apiRateLimitBucket() {
        // 분당 100회 요청 제한
        Bandwidth limit = Bandwidth.builder()
                .capacity(100)
                .refillIntervally(100, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    @Bean(name = "matchingCreateRateLimitBucket")
    public Bucket matchingCreateRateLimitBucket() {
        // 매칭 생성: 분당 10회 제한
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}


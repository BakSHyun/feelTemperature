package com.rstracker.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API Rate Limiting 인터셉터
 * 요청 횟수를 제한하여 서버 보호
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Bucket apiRateLimitBucket;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (apiRateLimitBucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"요청 횟수가 초과되었습니다.\"}");
            } catch (Exception e) {
                // ignore
            }
            return false;
        }
    }
}


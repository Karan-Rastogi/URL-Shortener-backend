package com.example.URLShortner.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Only rate limit the shorten endpoint
        if (!request.getRequestURI().equals("/api/urls/shorten") ||
                !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get client IP
        String ip = request.getRemoteAddr();
        String key = "rate:" + ip;

        // Increment counter in Redis
        Long count = redisTemplate.opsForValue().increment(key);

        // First request — set expiry window
        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        // Over limit — block request
        if (count != null && count > MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429," +
                            "\"message\":\"Too many requests. Try again in 60 seconds.\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
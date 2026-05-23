package com.narendra.paymentsystem.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 Every request gets:
 "X-Correlation-ID"

 Same ID flows through:
 logs
 kafka events
 retries
 websocket updates
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER =
            "X-Correlation-ID";

    public static final String MDC_KEY =
            "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Reuse existing correlation id if present
        String correlationId =
                request.getHeader(CORRELATION_ID_HEADER);

        // Generate new one if absent
        if (correlationId == null ||
                correlationId.isBlank()) {

            correlationId = UUID.randomUUID().toString();
        }

        // Store in MDC for logging
        MDC.put(MDC_KEY, correlationId);

        // Return correlation id in response header
        response.setHeader(
                CORRELATION_ID_HEADER,
                correlationId
        );

        try {

            filterChain.doFilter(request, response);

        } finally {

            MDC.clear();
        }
    }
}
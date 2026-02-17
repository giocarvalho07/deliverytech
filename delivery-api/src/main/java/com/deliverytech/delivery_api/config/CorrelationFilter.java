package com.deliverytech.delivery_api.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Gera um ID único para a requisição
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        // Adiciona o ID no Header da Resposta (Tarefa 3.3)
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse) response).setHeader(CORRELATION_ID_HEADER, correlationId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId"); // Limpa após a requisição
        }
    }
}
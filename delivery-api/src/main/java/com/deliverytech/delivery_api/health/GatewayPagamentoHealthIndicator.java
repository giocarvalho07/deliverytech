package com.deliverytech.delivery_api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GatewayPagamentoHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Simulação de verificação (ex: uma chamada HTTP rápida)
        boolean servicoOnline = verificarConectividade();

        if (servicoOnline) {
            return Health.up()
                    .withDetail("url", "https://api.pagamentos.com")
                    .withDetail("latencia", "45ms")
                    .build();
        }

        return Health.down()
                .withDetail("erro", "Timeout ao conectar com gateway de pagamento")
                .build();
    }

    private boolean verificarConectividade() {
        // Simula que o serviço está 90% das vezes online
        return new Random().nextDouble() > 0.1;
    }
}
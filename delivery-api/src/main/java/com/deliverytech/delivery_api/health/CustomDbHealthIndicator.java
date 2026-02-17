package com.deliverytech.delivery_api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomDbHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Aqui você poderia injetar o DataSource e validar algo específico
        return Health.up()
                .withDetail("banco", "H2 In-Memory")
                .withDetail("schema", "delivery_db")
                .build();
    }
}
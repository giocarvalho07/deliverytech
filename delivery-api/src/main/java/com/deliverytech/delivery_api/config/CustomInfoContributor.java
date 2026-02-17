package com.deliverytech.delivery_api.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("deployment", Map.of(
                "environment", "Development",
                "region", "Brazil-South",
                "startup_time", LocalDateTime.now()
        ));
    }
}

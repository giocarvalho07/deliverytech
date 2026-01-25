package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI deliveryApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Tech API")
                        .description("API do ecossistema Delivery Tech para gestão de restaurantes, produtos e pedidos.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Suporte Delivery Tech")
                                .email("suporte@deliverytech"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }

    // Grupo apenas para Endpoints de Gestão (Restaurantes e Produtos)
    @Bean
    public org.springdoc.core.models.GroupedOpenApi gestaoApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("1. Gestão e Cardápio")
                .pathsToMatch("/api/restaurantes/**", "/api/produtos/**")
                .build();
    }

    // Grupo para Operações de Pedidos e Clientes
    @Bean
    public org.springdoc.core.models.GroupedOpenApi operacaoApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("2. Operacional (Pedidos)")
                .pathsToMatch("/api/pedidos/**", "/api/clientes/**")
                .build();
    }

    // Grupo exclusivo para Relatórios (Atividade 1.4)
    @Bean
    public org.springdoc.core.models.GroupedOpenApi relatorioApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("3. Inteligência e Relatórios")
                .pathsToMatch("/api/relatorios/**")
                .build();
    }

}

package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Tech API")
                        .version("1.0")
                        .description("Documentação da API de Delivery com Spring Security e JWT")
                        .contact(new Contact()
                                .name("Suporte Delivery Tech")
                                .email("suporte@deliverytech.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP) // Classe do pacote .models
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    // Grupo Autenticação e Registro (Login, Cadastro e Perfil)
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. Autenticação e Registro")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    // Grupo apenas para Endpoints de Gestão (Restaurantes e Produtos)
    @Bean
    public org.springdoc.core.models.GroupedOpenApi gestaoApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("2. Gestão e Cardápio")
                .pathsToMatch("/api/restaurantes/**", "/api/produtos/**")
                .build();
    }

    // Grupo para Operações de Pedidos e Clientes
    @Bean
    public org.springdoc.core.models.GroupedOpenApi operacaoApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("3. Operacional (Pedidos)")
                .pathsToMatch("/api/pedidos/**", "/api/clientes/**")
                .build();
    }

    // Grupo exclusivo para Relatórios (Atividade 1.4)
    @Bean
    public org.springdoc.core.models.GroupedOpenApi relatorioApi() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("4. Inteligência e Relatórios")
                .pathsToMatch("/api/relatorios/**")
                .build();
    }
}

package com.deliverytech.delivery_api.dto.response.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String token;
    private String tipo; // Ex: "Bearer"
    private Long expiracaoHoras; // Ex: 24
    private UserResponseDTO usuario;
}
package com.deliverytech.delivery_api.dto.response.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String role;
}
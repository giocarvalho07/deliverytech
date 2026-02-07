package com.deliverytech.delivery_api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDTO {

    String token;
    String tipo; // Ex: "Bearer"
    String email;
    String role;

}

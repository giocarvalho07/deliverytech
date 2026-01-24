package com.deliverytech.delivery_api.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;
}
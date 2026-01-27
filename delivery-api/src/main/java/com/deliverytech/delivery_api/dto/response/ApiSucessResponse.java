package com.deliverytech.delivery_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiSucessResponse<T> {
    private boolean sucesso;
    private String mensagem;
    private T dados;
    private LocalDateTime timestamp;
}
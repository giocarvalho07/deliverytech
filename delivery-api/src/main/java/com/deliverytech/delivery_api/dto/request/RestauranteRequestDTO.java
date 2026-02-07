package com.deliverytech.delivery_api.dto.request;

import com.deliverytech.delivery_api.validation.validTelefone.ValidTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class RestauranteRequestDTO {

    @NotBlank(message = "O nome do restaurante é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Schema(example = "Pizzaria Bella Napoli", description = "Nome fantasia do restaurante")
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    @Schema(example = "Rua das nuvens", description = "Endereço do restaurante")
    private String endereco;

    @NotBlank(message = "A categoria é obrigatória")
    @Schema(example = "Italiana", description = "Categoria culinária")
    private String categoria;

    @NotBlank(message = "O telefone é obrigatório")
    @ValidTelefone
    @Schema(example = "11991929394", description = "Telefone celular")
    private String telefone;

    @NotNull(message = "A taxa de entrega é obrigatória")
    @PositiveOrZero(message = "A taxa de entrega deve ser um valor positivo")
    @Schema(example = "7.50", description = "Taxa fixa de entrega")
    private BigDecimal taxaEntrega;

    @NotNull(message = "O status de atividade deve ser informado")
    @Schema(example = "true", description = "Define se o restaurante aparece na listagem pública")
    private Boolean ativo;
}
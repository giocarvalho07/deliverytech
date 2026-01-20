package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique= true, nullable = false)
    private String email;

    private String telefone;

    private String endereco;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    private boolean ativo;

    @OneToMany(mappedBy="cliente", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

}

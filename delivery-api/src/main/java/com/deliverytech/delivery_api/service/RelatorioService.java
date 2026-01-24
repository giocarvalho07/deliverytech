package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.TotalVendasPorRestauranteDTO;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PedidoRepository respository;

    public List<TotalVendasPorRestauranteDTO> totalVendasPorRestaurante(){
        return respository.obterTotalVendasPorRestaurante();
    }

}

package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.response.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedidos;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void buscarPedidoPorId() {

        Pedido pedido = new Pedido();
        pedido.setId(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(modelMapper.map(any(), eq(PedidoResponseDTO.class))).thenReturn(new PedidoResponseDTO());

        var resultado = pedidoService.buscarPedidoPorId(1L);

        assertThat(resultado).isNotNull();
    }

    @Test
    void atualizarStatusPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus(StatusPedidos.PENDENTE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        pedidoService.atualizarStatusPedido(1L, StatusPedidos.CONFIRMADO);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedidos.CONFIRMADO);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void verificarSePedidoExisteNoRepository() {

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            pedidoService.buscarPedidoPorId(99L);
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }

        verify(pedidoRepository).findById(99L);
    }
}
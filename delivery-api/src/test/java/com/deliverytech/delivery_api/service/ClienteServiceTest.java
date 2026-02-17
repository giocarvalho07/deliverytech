package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ClienteRequestDTO;
import com.deliverytech.delivery_api.dto.response.ClienteResponseDTO;
import com.deliverytech.delivery_api.exeption.BusinessException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ModelMapper modelMapper;
    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve salvar um cliente com sucesso")
    void salvarClienteSucesso() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        // Usamos lenient() para o Mockito não reclamar se algum stub não for usado exatamente
        lenient().when(modelMapper.map(any(), eq(Cliente.class))).thenReturn(new Cliente());
        lenient().when(passwordEncoder.encode(any())).thenReturn("senha_criptografada");
        lenient().when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.cadastrarCliente(request);

        verify(clienteRepository, atLeastOnce()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para e-mail duplicado")
    void salvarClienteEmailDuplicado() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        // Forçamos o retorno de um cliente para simular que o e-mail já existe
        when(clienteRepository.findByEmail(any())).thenReturn(Optional.of(new Cliente()));

        assertThatThrownBy(() -> clienteService.cadastrarCliente(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID")
    void buscarPorIdExistente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        lenient().when(modelMapper.map(any(), eq(ClienteResponseDTO.class))).thenReturn(new ClienteResponseDTO());

        var resultado = clienteService.buscarClientePorId(1L);

        assertThat(resultado).isNotNull();
    }

}
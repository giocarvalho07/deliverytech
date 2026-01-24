package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ClienteRequestDTO;
import com.deliverytech.delivery_api.dto.response.ClienteResponseDTO;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    // cadastrarCliente(ClienteDTO dto) - Validar email único
    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO dto) {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um cliente cadastrado com este e-mail: " + dto.getEmail());
        }

        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(true);

        return modelMapper.map(clienteRepository.save(cliente), ClienteResponseDTO.class);
    }

    // buscarClientePorId(Long id) - Com tratamento de não encontrado
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // buscarClientePorEmail(String email) - Para login/autenticação
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o e-mail: " + email));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // listarClientesAtivos() - Apenas clientes ativos
    public List<ClienteResponseDTO> listarClientesAtivos() {
        return clienteRepository.findByAtivoTrue().stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // atualizarCliente(Long id, ClienteDTO dto) - Validar existência
    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não é possível atualizar: Cliente inexistente."));

        // Validação de e-mail duplicado ao trocar e-mail
        if (!clienteExistente.getEmail().equals(dto.getEmail()) &&
                clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("O novo e-mail já está em uso por outro cliente.");
        }

        // Atualiza os dados da entidade com os dados do DTO
        modelMapper.map(dto, clienteExistente);

        return modelMapper.map(clienteRepository.save(clienteExistente), ClienteResponseDTO.class);
    }

    // ativarDesativarCliente(Long id) - Toggle status ativo
    @Transactional
    public void ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        cliente.setAtivo(!cliente.isAtivo()); // Inverte o status atual (Toggle)
        clienteRepository.save(cliente);
    }
}
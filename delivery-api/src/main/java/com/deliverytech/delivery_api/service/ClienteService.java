package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ClienteRequestDTO;
import com.deliverytech.delivery_api.dto.response.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.response.PagedResponse;
import com.deliverytech.delivery_api.exeption.BusinessException;
import com.deliverytech.delivery_api.exeption.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    // cadastrarCliente - Valida email único
    @Transactional
    public ClienteResponseDTO cadastrarCliente(@Valid ClienteRequestDTO dto) {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um cliente cadastrado com este e-mail: " + dto.getEmail());
        }

        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(true);

        return modelMapper.map(clienteRepository.save(cliente), ClienteResponseDTO.class);
    }

    // buscarClientePorId
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + id));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // buscarClientePorEmail
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o e-mail: " + email));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // listarClientesAtivos - Apenas clientes ativos
    public PagedResponse<ClienteResponseDTO> listarClientesAtivosPaginado(Pageable pageable) {
        // Busca a página de entidades do banco
        Page<Cliente> paginaEntidade = clienteRepository.findByAtivoTrue(pageable);

        // Converte a página de Entidade para DTO mantendo a estrutura de página
        Page<ClienteResponseDTO> paginaDto = paginaEntidade
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class));
        // Envelopa no seu novo PagedResponse
        return new PagedResponse<>(paginaDto);
    }

    // atualizarCliente - Valida existência e e-mail único
    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não é possível atualizar: Cliente ID " + id + " não existe."));

        // Validação de e-mail duplicado ao trocar e-mail
        if (!clienteExistente.getEmail().equals(dto.getEmail()) &&
                clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("O novo e-mail informado já está em uso por outro cliente.");
        }

        modelMapper.map(dto, clienteExistente);
        return modelMapper.map(clienteRepository.save(clienteExistente), ClienteResponseDTO.class);
    }

    // ativarDesativarCliente - Toggle status
    @Transactional
    public void ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Falha ao alterar status: Cliente ID " + id + " não localizado."));

        cliente.setAtivo(!cliente.isAtivo());
        clienteRepository.save(cliente);
    }

}
package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
import com.deliverytech.delivery_api.exepction.BusinessException;
import com.deliverytech.delivery_api.exepction.EntityNotFoundException;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final ModelMapper modelMapper;

    public RestauranteService(RestauranteRepository restauranteRepository, ModelMapper modelMapper) {
        this.restauranteRepository = restauranteRepository;
        this.modelMapper = modelMapper;
    }

    // cadastrarRestaurante
    @Transactional
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteRequestDTO dto) {
        // Exemplo de validação de negócio: Taxa de entrega não pode ser negativa
        if (dto.getTaxaEntrega() != null && dto.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("A taxa de entrega não pode ser um valor negativo.");
        }

        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        return modelMapper.map(restauranteRepository.save(restaurante), RestauranteResponseDTO.class);
    }

    // buscarRestaurantePorId com tratamento de erro customizado
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com o ID: " + id));
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    // buscarRestaurantesPorCategoria - Filtro por categoria
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoria(categoria);
        return restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // buscarRestaurantesDisponiveis - Apenas ativos
    public List<RestauranteResponseDTO> buscarRestaurantesDisponiveis() {
        return restauranteRepository.findByAtivoTrue().stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // atualizarRestaurante - Validar existência e dados
    @Transactional
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteRequestDTO dto) {
        Restaurante restauranteExistente = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não foi possível atualizar: Restaurante ID " + id + " não existe."));

        // Garante que não altere para uma taxa negativa na atualização
        if (dto.getTaxaEntrega() != null && dto.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Valor de taxa de entrega inválido.");
        }

        modelMapper.map(dto, restauranteExistente);
        return modelMapper.map(restauranteRepository.save(restauranteExistente), RestauranteResponseDTO.class);
    }

    // calcularTaxaEntrega
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Cálculo de taxa abortado: Restaurante ID " + restauranteId + " não encontrado."));

        if (cep == null || cep.isBlank()) {
            throw new BusinessException("O CEP deve ser informado para o cálculo da taxa.");
        }

        BigDecimal taxaBase = restaurante.getTaxaEntrega();

        // Se o CEP começar com "0", é região local (taxa padrão)
        // Se começar com outro número, adicionamos uma taxa de distância
        if (cep.startsWith("0")) {
            return taxaBase;
        }

        return taxaBase.add(new BigDecimal("5.00"));
    }

    // Método para buscar por proximidade (Filtra se o CEP/prefixo está contido na String endereco)
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("O CEP deve ser informado para a busca de proximidade.");
        }

        // Pegamos os 5 primeiros dígitos ou o que for enviado para filtrar no texto do endereço
        String termoBusca = cep.length() > 5 ? cep.substring(0, 5) : cep;

        return restauranteRepository.findAll().stream()
                .filter(r -> r.getAtivo() != null && r.getAtivo()) // Garante que apenas ativos apareçam
                .filter(r -> r.getEndereco() != null && r.getEndereco().contains(termoBusca))
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Método para atualização parcial de status (PATCH)
    @Transactional
    public void atualizarStatus(Long id, boolean ativo) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com o ID: " + id));
        restaurante.setAtivo(ativo);
        restauranteRepository.save(restaurante);
    }

    // Método de listagem com filtros individuais
    public List<RestauranteResponseDTO> listarComFiltros(String categoria, Boolean ativo) {
        return restauranteRepository.findAll().stream()
                .filter(r -> (categoria == null || r.getCategoria().equalsIgnoreCase(categoria)))
                .filter(r -> (ativo == null || r.getAtivo().equals(ativo)))
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }
}
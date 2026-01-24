package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.response.RestauranteResponseDTO;
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

    // cadastrarRestaurante(RestauranteDTO dto)
    @Transactional
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteRequestDTO dto) {
        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        return modelMapper.map(restauranteRepository.save(restaurante), RestauranteResponseDTO.class);
    }

    // buscarRestaurantePorId(Long id) - Com tratamento de erro
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com o ID: " + id));
        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    // buscarRestaurantesPorCategoria(String categoria) - Filtro por categoria
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria).stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // buscarRestaurantesDisponiveis() - Apenas ativos
    public List<RestauranteResponseDTO> buscarRestaurantesDisponiveis() {
        return restauranteRepository.findByAtivoTrue().stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    // atualizarRestaurante(Long id, RestauranteDTO dto)
    @Transactional
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteRequestDTO dto) {
        Restaurante restauranteExistente = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado para atualização."));

        modelMapper.map(dto, restauranteExistente);
        return modelMapper.map(restauranteRepository.save(restauranteExistente), RestauranteResponseDTO.class);
    }

    // calcularTaxaEntrega(Long restauranteId, String cep)
    // Exemplo de lógica: Se o CEP for da mesma região, taxa base, senão taxa + adicional
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante inexistente."));

        BigDecimal taxaBase = restaurante.getTaxaEntrega();

        // Simulação de lógica por CEP: Se começar com "0", é entrega local
        if (cep.startsWith("0")) {
            return taxaBase;
        }
        return taxaBase.add(new BigDecimal("5.00")); // Adicional de distância
    }
}
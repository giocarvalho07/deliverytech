package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.response.ProdutoResponseDTO;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ModelMapper modelMapper;

    public ProdutoService(ProdutoRepository produtoRepository,
                          RestauranteRepository restauranteRepository,
                          ModelMapper modelMapper) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.modelMapper = modelMapper;
    }

    // cadastrarProduto(ProdutoDTO dto) - Validar restaurante existe
    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Não é possível cadastrar produto: Restaurante não encontrado."));

        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setRestaurante(restaurante);

        return modelMapper.map(produtoRepository.save(produto), ProdutoResponseDTO.class);
    }

    // buscarProdutosPorRestaurante(Long restauranteId) - Apenas disponíveis
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId).stream()
                .filter(Produto::getDisponivel) // Filtra apenas os disponíveis
                .map(p -> modelMapper.map(p, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    // buscarProdutoPorId(Long id) - Com validação de disponibilidade
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        if (!produto.getDisponivel()) {
            throw new RuntimeException("Este produto não está disponível no momento.");
        }

        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    // atualizarProduto(Long id, ProdutoDTO dto)
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente."));

        modelMapper.map(dto, produtoExistente);
        // Se o restauranteId mudou no DTO, atualizamos a referência
        if (!produtoExistente.getRestaurante().getId().equals(dto.getRestauranteId())) {
            Restaurante novoRestaurante = restauranteRepository.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new RuntimeException("Novo Restaurante não encontrado."));
            produtoExistente.setRestaurante(novoRestaurante);
        }

        return modelMapper.map(produtoRepository.save(produtoExistente), ProdutoResponseDTO.class);
    }

    // alterarDisponibilidade(Long id, boolean disponivel) - Toggle
    @Transactional
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    // buscarProdutosPorCategoria(String categoria)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria).stream()
                .map(p -> modelMapper.map(p, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
}

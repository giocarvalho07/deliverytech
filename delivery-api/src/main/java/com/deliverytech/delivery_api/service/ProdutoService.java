package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.response.ProdutoResponseDTO;
import com.deliverytech.delivery_api.exepction.BusinessException;
import com.deliverytech.delivery_api.exepction.EntityNotFoundException;
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

    // cadastrarProduto - Validar se o restaurante existe
    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Erro ao cadastrar produto: Restaurante ID " + dto.getRestauranteId() + " não encontrado."));

        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setId(null);
        produto.setRestaurante(restaurante);

        return modelMapper.map(produtoRepository.save(produto), ProdutoResponseDTO.class);
    }

    // buscarProdutosPorRestaurante - Apenas disponíveis
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não encontrado.");
        }

        return produtoRepository.findByRestauranteId(restauranteId).stream()
                .filter(Produto::getDisponivel)
                .map(p -> modelMapper.map(p, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    // buscarProdutoPorId
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto ID " + id + " não encontrado."));

        if (!produto.getDisponivel()) {
            throw new BusinessException("O produto '" + produto.getNome() + "' não está disponível no cardápio no momento.");
        }

        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    // atualizarProduto
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Falha na atualização: Produto não localizado."));

        modelMapper.map(dto, produtoExistente);

        // Valida troca de restaurante caso ocorra
        if (dto.getRestauranteId() != null && !produtoExistente.getRestaurante().getId().equals(dto.getRestauranteId())) {
            Restaurante novoRestaurante = restauranteRepository.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Novo Restaurante informado não existe."));
            produtoExistente.setRestaurante(novoRestaurante);
        }

        return modelMapper.map(produtoRepository.save(produtoExistente), ProdutoResponseDTO.class);
    }

    // alterarDisponibilidade - Toggle disponibilidade
    @Transactional
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Impossível alterar disponibilidade: Produto não encontrado."));

        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    // buscarProdutosPorCategoria - Filtro por categoria
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria).stream()
                .map(p -> modelMapper.map(p, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    // DELETE /api/produtos/{id} - NOVO
    @Transactional
    public void removerProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Não é possível remover: Produto ID " + id + " não encontrado.");
        }
        produtoRepository.deleteById(id);
    }

    // GET /api/produtos/buscar?nome={nome} - NOVO
    public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {
        // Assume-se que o repository tem o método findByNomeContainingIgnoreCase
        return produtoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(p -> modelMapper.map(p, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
}
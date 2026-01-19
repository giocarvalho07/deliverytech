package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar
    @Transactional
    public Produto salvar(Produto produto) {
        if (produto.getDisponivel() == null) {
            produto.setDisponivel(true);
        }
        return produtoRepository.save(produto);
    }

    // Listar todos os produtos
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // Buscar por ID
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
    }

    // Buscar produtos de um Restaurante específico
    public List<Produto> listarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    // Atualizar
    @Transactional
    public Produto atualizar(Long id, Produto dadosAtualizados) {
        Produto produtoExistente = buscarPorId(id);

        produtoExistente.setNome(dadosAtualizados.getNome());
        produtoExistente.setDescricao(dadosAtualizados.getDescricao());
        produtoExistente.setCategoria(dadosAtualizados.getCategoria());
        produtoExistente.setPreco(dadosAtualizados.getPreco());
        produtoExistente.setDisponivel(dadosAtualizados.getDisponivel());

        return produtoRepository.save(produtoExistente);
    }

    // Deletar (Físico) ou Inativar
    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }
}

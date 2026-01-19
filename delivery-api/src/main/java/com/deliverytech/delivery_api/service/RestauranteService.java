package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RestauranteService {
    @Autowired
    private RestauranteRepository restauranteRepository;

    // Criar
    @Transactional
    public Restaurante salvar(Restaurante restaurante) {
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    // Listar Todos Ativos
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findByAtivoTrue();
    }

    // Buscar por ID
    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com o ID: " + id));
    }

    // Buscar por Categoria
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategory(categoria);
    }

    // Atualizar
    @Transactional
    public Restaurante atualizar(Long id, Restaurante dadosAtualizados) {
        Restaurante restauranteExistente = buscarPorId(id);

        restauranteExistente.setNome(dadosAtualizados.getNome());
        restauranteExistente.setCategoria(dadosAtualizados.getCategoria());
        restauranteExistente.setEndereco(dadosAtualizados.getEndereco());
        restauranteExistente.setTelefone(dadosAtualizados.getTelefone());
        restauranteExistente.setTaxaEntrega(dadosAtualizados.getTaxaEntrega());
        // A avaliação geralmente é atualizada por outro processo, mas incluímos se necessário
        restauranteExistente.setAvaliação(dadosAtualizados.getAvaliação());

        return restauranteRepository.save(restauranteExistente);
    }

    // Inativar (Delete Lógico)
    @Transactional
    public void inativar(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }
}

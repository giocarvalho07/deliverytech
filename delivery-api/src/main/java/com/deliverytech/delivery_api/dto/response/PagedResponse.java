package com.deliverytech.delivery_api.dto.response;

import lombok.Data;
import org.springframework.data.domain.Page;
import java.util.List;


@Data
public class PagedResponse<T> {
    private List<T> conteudo;
    private int paginaAtual;
    private int tamanhoPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean ultima;

    public PagedResponse(Page<T> page) {
        this.conteudo = page.getContent();
        this.paginaAtual = page.getNumber();
        this.tamanhoPagina = page.getSize();
        this.totalElementos = page.getTotalElements();
        this.totalPaginas = page.getTotalPages();
        this.ultima = page.isLast();
    }
}
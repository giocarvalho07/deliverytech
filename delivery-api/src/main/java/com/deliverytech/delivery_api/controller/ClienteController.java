package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ClienteRequestDTO;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.ClienteResponseDTO;
import com.deliverytech.delivery_api.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestão de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // POST /api/clientes - Cadastrar cliente
    @Operation(summary = "Cadastrar novo cliente", description = "Cria um novo cliente no sistema com status ativo por padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiSucessResponse<ClienteResponseDTO>> cadastrar(@Valid @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO novoCliente = clienteService.cadastrarCliente(dto);
        // Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoCliente.getId())
                .toUri();

        // Aqui o Java usará o seu DTO importado corretamente
        ApiSucessResponse<ClienteResponseDTO> response =
                ApiSucessResponse.<ClienteResponseDTO>builder()
                        .sucesso(true)
                        .mensagem("Cliente cadastrado com sucesso")
                        .dados(novoCliente)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.created(uri).body(response);
    }

    // GET /api/clientes/{id} - Buscar por ID
    @Operation(summary = "Listar clientes por ID", description = "Lista um novo cliente no sistema com filtros de ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<ClienteResponseDTO>> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO dto = clienteService.buscarClientePorId(id);

        return ResponseEntity.ok(ApiSucessResponse.<ClienteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Cliente localizado")
                .dados(dto)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // GET /api/clientes - Listar clientes ativos
    @Operation(summary = "Listar clientes", description = "Lista um novo cliente ativo no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping
    public ResponseEntity<ApiSucessResponse<List<ClienteResponseDTO>>> listarAtivos() {
        List<ClienteResponseDTO> lista = clienteService.listarClientesAtivos();

        return ResponseEntity.ok(ApiSucessResponse.<List<ClienteResponseDTO>>builder()
                .sucesso(true)
                .mensagem("Lista de clientes ativos")
                .dados(lista)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PUT /api/clientes/{id} - Atualizar cliente
    @Operation(summary = "Atualizar clientes por ID", description = "Atualiza um cliente no sistema com novas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiSucessResponse<ClienteResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO atualizado = clienteService.atualizarCliente(id, dto);

        return ResponseEntity.ok(ApiSucessResponse.<ClienteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Cliente atualizado")
                .dados(atualizado)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // PATCH /api/clientes/{id}/status - Ativar/desativar (Toggle)
    @Operation(summary = "Inativar clientes", description = "Inativar um cliente no sistema com alterações parciais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id) {
        clienteService.ativarDesativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/clientes/email/{email} - Buscar por email
    @Operation(summary = "Buscar cliente por email", description = "Buscar um cliente no sistema por email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente por email encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente por email não encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiSucessResponse<ClienteResponseDTO>> buscarPorEmail(@PathVariable String email) {
        ClienteResponseDTO dto = clienteService.buscarClientePorEmail(email);

        return ResponseEntity.ok(ApiSucessResponse.<ClienteResponseDTO>builder()
                .sucesso(true)
                .mensagem("Cliente encontrado")
                .dados(dto)
                .timestamp(LocalDateTime.now())
                .build());
    }
}

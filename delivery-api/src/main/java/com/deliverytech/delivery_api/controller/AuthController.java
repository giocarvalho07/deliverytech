package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.login.LoginRequestDTO;
import com.deliverytech.delivery_api.dto.request.login.RegisterRequestDTO;
import com.deliverytech.delivery_api.dto.response.ApiErrorResponse;
import com.deliverytech.delivery_api.dto.response.ApiSucessResponse;
import com.deliverytech.delivery_api.dto.response.login.LoginResponseDTO;
import com.deliverytech.delivery_api.dto.response.login.UserResponseDTO;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.deliverytech.delivery_api.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Operation(summary = "Login clientes", description = "Logar um cliente no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente logado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não logado")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getSenha());
            var authentication = manager.authenticate(authenticationToken);

            var usuario = (Usuario) authentication.getPrincipal();
            var tokenJWT = tokenService.gerarToken(usuario);

            UserResponseDTO userDto = UserResponseDTO.builder()
                    .id(usuario.getId())
                    .nome(usuario.getNome())
                    .email(usuario.getEmail())
                    .role(usuario.getRole().name())
                    .build();

            LoginResponseDTO loginData = LoginResponseDTO.builder()
                    .token(tokenJWT)
                    .tipo("Bearer")
                    .expiracaoHoras(24L)
                    .usuario(userDto)
                    .build();

            return ResponseEntity.ok(ApiSucessResponse.<LoginResponseDTO>builder()
                    .sucesso(true)
                    .mensagem("Login realizado com sucesso")
                    .dados(loginData)
                    .timestamp(LocalDateTime.now())
                    .build());

        } catch (BadCredentialsException e) {
            // Ajustado para o construtor: (status, mensagem, timestamp, erros)
            return ResponseEntity.status(401).body(new ApiErrorResponse(
                    401,
                    "E-mail ou senha inválidos",
                    LocalDateTime.now(),
                    null // Sem mapa de erros específicos aqui
            ));
        }
    }

    @Operation(summary = "Cadastrar clientes", description = "Cadastrar um cliente no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO data) {
        // Validar se e-mail não existe
        if (usuarioRepository.findByEmail(data.getEmail()).isPresent()) {
            // Ajustado para o construtor: (status, mensagem, timestamp, erros)
            return ResponseEntity.status(400).body(new ApiErrorResponse(
                    400,
                    "Este e-mail já está cadastrado no sistema",
                    LocalDateTime.now(),
                    null
            ));
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(data.getNome());
        novoUsuario.setEmail(data.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(data.getSenha())); //Criptografar com BCrypt
        novoUsuario.setRole(data.getRole());

        usuarioRepository.save(novoUsuario);

        UserResponseDTO userDto = UserResponseDTO.builder()
                .id(novoUsuario.getId())
                .nome(novoUsuario.getNome())
                .email(novoUsuario.getEmail())
                .role(novoUsuario.getRole().name())
                .build();

        //Retornar UserResponse formatado
        return ResponseEntity.status(201).body(ApiSucessResponse.<UserResponseDTO>builder()
                .sucesso(true)
                .mensagem("Usuário registrado com sucesso")
                .dados(userDto)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "Auth /me", description = "Dados do perfil recuperado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados recuperados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Dados não recuperados")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiSucessResponse<UserResponseDTO>> me() {
        // Extrair informações do usuário logado
        var usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserResponseDTO userDto = UserResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole().name())
                .build();

        return ResponseEntity.ok(ApiSucessResponse.<UserResponseDTO>builder()
                .sucesso(true)
                .mensagem("Dados do perfil recuperados")
                .dados(userDto)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
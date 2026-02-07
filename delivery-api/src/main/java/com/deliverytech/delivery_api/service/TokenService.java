package com.deliverytech.delivery_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.deliverytech.delivery_api.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Function;


@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    // Gerar token com Claims customizados e expiração de 24h
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("delivery-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("userId", usuario.getId()) // Claim customizado
                    .withClaim("role", usuario.getRole().name()) // Claim customizado
                    .withClaim("restauranteId", usuario.getRestauranteId()) // Claim customizado
                    .withExpiresAt(gerarDataExpiracao()) // 24 horas
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    // Validar token e extrair o Subject (email)
    public String validarToken(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("delivery-api")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return ""; // Retorna vazio para indicar falha na validação
        }
    }

    // Método genérico para extrair claims (exigência da tarefa)
    public <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        DecodedJWT jwt = JWT.decode(token);
        return claimsResolver.apply(jwt);
    }

    public String extractUsername(String token) {
        return extractClaim(token, DecodedJWT::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt).before(new Date());
    }

    private Instant gerarDataExpiracao() {
        // Definir tempo de expiração para 24 horas
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}
package br.com.runly.dto;

public record AuthResponse(
        String token,
        UsuarioResponse usuario
) {
}

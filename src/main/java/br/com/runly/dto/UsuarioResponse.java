package br.com.runly.dto;

import br.com.runly.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String fotoPerfil,
        String bio,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFotoPerfil(),
                usuario.getBio(),
                usuario.getCriadoEm()
        );
    }
}

package br.com.runly.dto;

import br.com.runly.model.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String texto,
        String imagemUrl,
        LocalDateTime dataCriacao,
        UsuarioResponse usuario
) {
    public static PostResponse fromEntity(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTexto(),
                post.getImagemUrl(),
                post.getDataCriacao(),
                UsuarioResponse.fromEntity(post.getUsuario())
        );
    }
}

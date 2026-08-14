package br.com.runly.dto;

import br.com.runly.model.grupo.MensagemGrupo;

import java.time.LocalDateTime;

public record MensagemGrupoResponse(
        long id,
        long grupoId,
        String texto,
        LocalDateTime dataEnvio,
        UsuarioResponse autor
) {
    public static MensagemGrupoResponse fromEntity(MensagemGrupo mensagem) {
        return new MensagemGrupoResponse(
                mensagem.getId(),
                mensagem.getGrupo() != null ? mensagem.getGrupo().getId() : 0,
                mensagem.getTexto(),
                mensagem.getDataEnvio(),
                mensagem.getUsuario() != null ? UsuarioResponse.fromEntity(mensagem.getUsuario()) : null
        );
    }
}

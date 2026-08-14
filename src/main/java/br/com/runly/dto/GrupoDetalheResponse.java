package br.com.runly.dto;

import br.com.runly.model.Usuario;
import br.com.runly.model.grupo.Grupo;

import java.util.Collections;
import java.util.List;

public record GrupoDetalheResponse(
        long id,
        String nome,
        String descricao,
        String fotoPerfil,
        UsuarioResponse fundador,
        List<UsuarioResponse> membros,
        List<UsuarioResponse> administradores,
        int totalMembros,
        int totalAdministradores,
        boolean ehMembro,
        boolean ehAdministrador,
        boolean ehFundador
) {
    public static GrupoDetalheResponse fromEntity(Grupo grupo, Usuario usuarioLogado) {
        List<UsuarioResponse> membrosDto = grupo.getMembros() != null
                ? grupo.getMembros().stream().map(UsuarioResponse::fromEntity).toList()
                : Collections.emptyList();

        List<UsuarioResponse> adminsDto = grupo.getAdministradores() != null
                ? grupo.getAdministradores().stream().map(UsuarioResponse::fromEntity).toList()
                : Collections.emptyList();

        boolean ehFundador = usuarioLogado != null && grupo.getFundador() != null
                && grupo.getFundador().getId() == usuarioLogado.getId();

        boolean ehMembro = usuarioLogado != null && grupo.getMembros() != null
                && grupo.getMembros().stream().anyMatch(m -> m.getId() == usuarioLogado.getId());

        boolean ehAdmin = usuarioLogado != null && grupo.getAdministradores() != null
                && grupo.getAdministradores().stream().anyMatch(a -> a.getId() == usuarioLogado.getId());

        return new GrupoDetalheResponse(
                grupo.getId(),
                grupo.getNome(),
                grupo.getDescricao(),
                grupo.getFotoPerfil(),
                grupo.getFundador() != null ? UsuarioResponse.fromEntity(grupo.getFundador()) : null,
                membrosDto,
                adminsDto,
                grupo.getMembros() != null ? grupo.getMembros().size() : 0,
                grupo.getAdministradores() != null ? grupo.getAdministradores().size() : 0,
                ehMembro,
                ehAdmin,
                ehFundador
        );
    }
}

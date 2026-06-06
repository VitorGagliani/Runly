package br.com.runly.dto;

import br.com.runly.model.grupo.Grupo;

public record GrupoResponse(
        long id,
        String nome,
        String descricao,
        String fotoPerfil,
        UsuarioResponse fundador,
        int totalMembros,
        int totalAdministradores
) {

    public static GrupoResponse fromEntity(Grupo grupo) {
        return new GrupoResponse(
                grupo.getId(),
                grupo.getNome(),
                grupo.getDescricao(),
                grupo.getFotoPerfil(),
                grupo.getFundador() != null ? UsuarioResponse.fromEntity(grupo.getFundador()) : null,
                grupo.getMembros() != null ? grupo.getMembros().size() : 0,
                grupo.getAdministradores() != null ? grupo.getAdministradores().size() : 0
        );
    }
}
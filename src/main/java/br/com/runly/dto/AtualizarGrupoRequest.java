package br.com.runly.dto;

public record AtualizarGrupoRequest(
        String nome,
        String descricao,
        String fotoPerfil
) {
}
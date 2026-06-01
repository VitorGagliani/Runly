package br.com.runly.dto;

import jakarta.validation.constraints.Size;

public record AtualizarPerfilRequest(
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,

        String fotoPerfil,

        @Size(max = 300, message = "Bio deve ter no máximo 300 caracteres")
        String bio
) {
}

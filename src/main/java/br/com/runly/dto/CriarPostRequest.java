package br.com.runly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarPostRequest(
        @NotBlank(message = "Texto é obrigatório")
        @Size(max = 500, message = "Post deve ter no máximo 500 caracteres")
        String texto,

        String imagemUrl
) {
}

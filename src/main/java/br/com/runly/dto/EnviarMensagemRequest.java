package br.com.runly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnviarMensagemRequest(
        @NotBlank(message = "O texto da mensagem não pode ficar em branco")
        @Size(max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres")
        String texto
) {
}

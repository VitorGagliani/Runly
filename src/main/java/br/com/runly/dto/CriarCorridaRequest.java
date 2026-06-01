package br.com.runly.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CriarCorridaRequest(
        @NotNull(message = "Distância é obrigatória")
        @DecimalMin(value = "0.01", message = "Distância deve ser maior que zero")
        Double distanciaKm,

        @NotNull(message = "Tempo é obrigatório")
        @Min(value = 1, message = "Tempo deve ser maior que zero")
        Integer tempoSegundos,

        LocalDateTime dataCorrida,
        String rotaJson
) {
}

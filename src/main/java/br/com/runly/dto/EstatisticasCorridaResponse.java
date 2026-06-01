package br.com.runly.dto;

public record EstatisticasCorridaResponse(
        Integer quantidadeCorridas,
        Double distanciaTotalKm,
        Integer tempoTotalSegundos,
        Double paceMedioGeral
) {
}

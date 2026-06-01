package br.com.runly.dto;

import br.com.runly.model.Corrida;

import java.time.LocalDateTime;

public record CorridaResponse(
        Long id,
        Double distanciaKm,
        Integer tempoSegundos,
        Double paceMedio,
        LocalDateTime dataCorrida,
        String rotaJson,
        UsuarioResponse usuario
) {
    public static CorridaResponse fromEntity(Corrida corrida) {
        return new CorridaResponse(
                corrida.getId(),
                corrida.getDistanciaKm(),
                corrida.getTempoSegundos(),
                corrida.getPaceMedio(),
                corrida.getDataCorrida(),
                corrida.getRotaJson(),
                UsuarioResponse.fromEntity(corrida.getUsuario())
        );
    }
}

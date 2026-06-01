package br.com.runly.service;

import br.com.runly.dto.CorridaResponse;
import br.com.runly.dto.CriarCorridaRequest;
import br.com.runly.dto.EstatisticasCorridaResponse;
import br.com.runly.model.Corrida;
import br.com.runly.model.Usuario;
import br.com.runly.repository.CorridaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CorridaService {

    private final CorridaRepository corridaRepository;
    private final UsuarioService usuarioService;

    public CorridaService(CorridaRepository corridaRepository, UsuarioService usuarioService) {
        this.corridaRepository = corridaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public CorridaResponse criar(String emailUsuario, CriarCorridaRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        Corrida corrida = new Corrida();
        corrida.setUsuario(usuario);
        corrida.setDistanciaKm(request.distanciaKm());
        corrida.setTempoSegundos(request.tempoSegundos());
        corrida.setDataCorrida(request.dataCorrida());
        corrida.setRotaJson(request.rotaJson());
        corrida.setPaceMedio(calcularPace(request.distanciaKm(), request.tempoSegundos()));

        Corrida corridaSalva = corridaRepository.save(corrida);
        return CorridaResponse.fromEntity(corridaSalva);
    }

    public List<CorridaResponse> listarMinhas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        return corridaRepository.findByUsuarioOrderByDataCorridaDesc(usuario)
                .stream()
                .map(CorridaResponse::fromEntity)
                .toList();
    }

    public EstatisticasCorridaResponse buscarEstatisticas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        List<Corrida> corridas = corridaRepository.findByUsuarioOrderByDataCorridaDesc(usuario);

        int quantidade = corridas.size();
        double distanciaTotal = corridas.stream()
                .mapToDouble(Corrida::getDistanciaKm)
                .sum();
        int tempoTotal = corridas.stream()
                .mapToInt(Corrida::getTempoSegundos)
                .sum();

        double paceMedioGeral = distanciaTotal > 0 ? calcularPace(distanciaTotal, tempoTotal) : 0.0;

        return new EstatisticasCorridaResponse(
                quantidade,
                arredondar(distanciaTotal),
                tempoTotal,
                paceMedioGeral
        );
    }

    private double calcularPace(double distanciaKm, int tempoSegundos) {
        double minutos = tempoSegundos / 60.0;
        return arredondar(minutos / distanciaKm);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

package br.com.runly.controller;

import br.com.runly.dto.CorridaResponse;
import br.com.runly.dto.CriarCorridaRequest;
import br.com.runly.dto.EstatisticasCorridaResponse;
import br.com.runly.service.corrida.CorridaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/corridas")
public class CorridaController {

    private final CorridaService corridaService;

    public CorridaController(CorridaService corridaService) {
        this.corridaService = corridaService;
    }

    @PostMapping
    public ResponseEntity<CorridaResponse> criar(
            Authentication authentication,
            @RequestBody @Valid CriarCorridaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corridaService.criar(authentication.getName(), request));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<CorridaResponse>> listarMinhas(Authentication authentication) {
        return ResponseEntity.ok(corridaService.listarMinhas(authentication.getName()));
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasCorridaResponse> buscarEstatisticas(Authentication authentication) {
        return ResponseEntity.ok(corridaService.buscarEstatisticas(authentication.getName()));
    }
}

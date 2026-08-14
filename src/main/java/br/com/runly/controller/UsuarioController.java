package br.com.runly.controller;

import br.com.runly.dto.AtualizarPerfilRequest;
import br.com.runly.dto.UsuarioResponse;
import br.com.runly.service.auth.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarMeuPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.buscarPerfilLogado(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMeuPerfil(
            Authentication authentication,
            @RequestBody @Valid AtualizarPerfilRequest request
    ) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(authentication.getName(), request));
    }

    @org.springframework.web.bind.annotation.PostMapping("/me/foto")
    public ResponseEntity<UsuarioResponse> uploadFotoPerfil(
            @org.springframework.web.bind.annotation.RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            Authentication authentication
    ) {
        return ResponseEntity.ok(usuarioService.uploadFotoPerfil(authentication.getName(), arquivo));
    }

    @GetMapping("/buscar")
    public ResponseEntity<java.util.List<UsuarioResponse>> buscarUsuarios(
            @org.springframework.web.bind.annotation.RequestParam("q") String termo
    ) {
        return ResponseEntity.ok(usuarioService.buscarUsuarios(termo));
    }
}

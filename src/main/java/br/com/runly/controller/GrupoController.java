package br.com.runly.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.runly.dto.AtualizarGrupoRequest;
import br.com.runly.dto.CriarGrupoRequest;
import br.com.runly.dto.GrupoResponse;
import br.com.runly.service.grupo.GrupoService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> criarGrupo(
            @RequestBody CriarGrupoRequest dto,
            Principal principal
    ) {
        GrupoResponse response = grupoService.criarGrupo(principal.getName(), dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{grupoId}/foto")
    public ResponseEntity<GrupoResponse> uploadFotoGrupo(
            @PathVariable Long grupoId,
            @RequestParam("arquivo") MultipartFile arquivo,
            Principal principal
    ) {
        GrupoResponse response = grupoService.uploadFotoGrupo(
                grupoId,
                principal.getName(),
                arquivo
        );

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{grupoId}")
    public ResponseEntity<GrupoResponse> editarGrupo(
            @PathVariable Long grupoId,
            @RequestBody AtualizarGrupoRequest dto,
            Principal principal
    ) {
        GrupoResponse response = grupoService.editarGrupo(grupoId, principal.getName(), dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{grupoId}")
    public ResponseEntity<Void> excluirGrupo(
            @PathVariable Long grupoId,
            Principal principal
    ) {
        grupoService.excluirGrupo(grupoId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{grupoId}/entrar")
    public ResponseEntity<GrupoResponse> entrarNoGrupo(
            @PathVariable Long grupoId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.entrarNoGrupo(grupoId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{grupoId}/sair")
    public ResponseEntity<GrupoResponse> sairDoGrupo(
            @PathVariable Long grupoId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.sairDoGrupo(grupoId, principal.getName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<GrupoResponse>> listarGrupos() {
        return ResponseEntity.ok(grupoService.listarGrupos());
    }

    @PostMapping("/{grupoId}/membros/{usuarioId}")
    public ResponseEntity<GrupoResponse> adicionarMembro(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.adicionarMembro(
                grupoId,
                usuarioId,
                principal.getName()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{grupoId}/membros/{usuarioId}")
    public ResponseEntity<GrupoResponse> removerMembro(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.removerMembro(
                grupoId,
                usuarioId,
                principal.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{grupoId}/administradores/{usuarioId}")
    public ResponseEntity<GrupoResponse> promoverAdministrador(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.promoverAdministrador(
                grupoId,
                usuarioId,
                principal.getName()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{grupoId}/administradores/{usuarioId}")
    public ResponseEntity<GrupoResponse> removerAdministrador(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId,
            Principal principal
    ) {
        GrupoResponse response = grupoService.removerAdministrador(
                grupoId,
                usuarioId,
                principal.getName()
        );

        return ResponseEntity.ok(response);
    }
}
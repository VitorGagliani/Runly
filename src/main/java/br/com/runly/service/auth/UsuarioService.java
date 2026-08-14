package br.com.runly.service.auth;

import br.com.runly.dto.AtualizarPerfilRequest;
import br.com.runly.dto.UsuarioResponse;
import br.com.runly.exception.RegraNegocioException;
import br.com.runly.model.Usuario;
import br.com.runly.repository.UsuarioRepository;
import br.com.runly.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UploadService uploadService;

    public UsuarioService(UsuarioRepository usuarioRepository, UploadService uploadService) {
        this.usuarioRepository = usuarioRepository;
        this.uploadService = uploadService;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
    }

    public UsuarioResponse buscarPerfilLogado(String email) {
        return UsuarioResponse.fromEntity(buscarPorEmail(email));
    }

    @Transactional
    public UsuarioResponse uploadFotoPerfil(String email, MultipartFile arquivo) {
        Usuario usuario = buscarPorEmail(email);
        String urlFoto = uploadService.salvarFotoPerfil(usuario.getId(), arquivo);
        usuario.setFotoPerfil(urlFoto);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(String email, AtualizarPerfilRequest request) {
        Usuario usuario = buscarPorEmail(email);

        if (request.nome() != null && !request.nome().isBlank()) {
            usuario.setNome(request.nome());
        }

        if (request.fotoPerfil() != null) {
            usuario.setFotoPerfil(request.fotoPerfil());
        }

        if (request.bio() != null) {
            usuario.setBio(request.bio());
        }

        return UsuarioResponse.fromEntity(usuario);
    }

    public java.util.List<UsuarioResponse> buscarUsuarios(String termo) {
        if (termo == null || termo.trim().length() < 2) {
            return java.util.Collections.emptyList();
        }
        return usuarioRepository.buscarPorNomeOuEmail(termo.trim())
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }
}

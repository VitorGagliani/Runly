package br.com.runly.service.auth;

import br.com.runly.dto.AtualizarPerfilRequest;
import br.com.runly.dto.UsuarioResponse;
import br.com.runly.exception.RegraNegocioException;
import br.com.runly.model.Usuario;
import br.com.runly.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
    }

    public UsuarioResponse buscarPerfilLogado(String email) {
        return UsuarioResponse.fromEntity(buscarPorEmail(email));
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
}

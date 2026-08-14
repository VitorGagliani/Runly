package br.com.runly.service.grupo;

import br.com.runly.dto.EnviarMensagemRequest;
import br.com.runly.dto.MensagemGrupoResponse;
import br.com.runly.model.Usuario;
import br.com.runly.model.grupo.Grupo;
import br.com.runly.model.grupo.MensagemGrupo;
import br.com.runly.repository.GrupoRepository;
import br.com.runly.repository.MensagemGrupoRepository;
import br.com.runly.service.auth.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensagemGrupoService {

    private final MensagemGrupoRepository mensagemRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioService usuarioService;

    public MensagemGrupoService(
            MensagemGrupoRepository mensagemRepository,
            GrupoRepository grupoRepository,
            UsuarioService usuarioService
    ) {
        this.mensagemRepository = mensagemRepository;
        this.grupoRepository = grupoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public MensagemGrupoResponse enviarMensagem(Long grupoId, String emailUsuario, EnviarMensagemRequest request) {
        Grupo grupo = buscarGrupo(grupoId);
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        validarMembroDoGrupo(grupo, usuario);

        MensagemGrupo mensagem = new MensagemGrupo();
        mensagem.setGrupo(grupo);
        mensagem.setUsuario(usuario);
        mensagem.setTexto(request.texto().trim());

        MensagemGrupo salva = mensagemRepository.save(mensagem);
        return MensagemGrupoResponse.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<MensagemGrupoResponse> listarMensagens(Long grupoId, String emailUsuario) {
        Grupo grupo = buscarGrupo(grupoId);
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        validarMembroDoGrupo(grupo, usuario);

        return mensagemRepository.findByGrupoIdOrderByDataEnvioAsc(grupoId)
                .stream()
                .map(MensagemGrupoResponse::fromEntity)
                .toList();
    }

    private Grupo buscarGrupo(Long grupoId) {
        return grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));
    }

    private void validarMembroDoGrupo(Grupo grupo, Usuario usuario) {
        boolean ehMembro = grupo.getMembros() != null &&
                grupo.getMembros().stream().anyMatch(m -> m.getId() == usuario.getId());

        if (!ehMembro) {
            throw new RuntimeException("Acesso negado: Você precisa ser membro do grupo para interagir no chat.");
        }
    }
}

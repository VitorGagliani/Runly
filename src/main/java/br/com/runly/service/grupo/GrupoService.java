package br.com.runly.service.grupo;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.runly.dto.AtualizarGrupoRequest;
import br.com.runly.dto.CriarGrupoRequest;
import br.com.runly.dto.GrupoResponse;
import br.com.runly.model.Usuario;
import br.com.runly.model.grupo.Grupo;
import br.com.runly.repository.GrupoRepository;
import br.com.runly.repository.UsuarioRepository;
import br.com.runly.service.auth.UsuarioService;

import org.springframework.web.multipart.MultipartFile;
import br.com.runly.service.UploadService;

@Service
public class GrupoService {

    private final GrupoRepository grupoRep;
    private final UsuarioRepository usuarioRep;
    private final UsuarioService user;
    private final UploadService uploadService;

    public GrupoService(
            GrupoRepository grupoRep,
            UsuarioRepository usuarioRep,
            UsuarioService user,
            UploadService uploadService
    ) {
        this.grupoRep = grupoRep;
        this.usuarioRep = usuarioRep;
        this.user = user;
        this.uploadService = uploadService;
    }

    public GrupoResponse criarGrupo(String emailUsuario, CriarGrupoRequest dto) {

        Usuario usuario = user.buscarPorEmail(emailUsuario);

        Grupo grupo = new Grupo();
        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());
        grupo.setFundador(usuario);

        grupo.getMembros().add(usuario);
        grupo.getAdministradores().add(usuario);

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }
    public GrupoResponse uploadFotoGrupo(Long grupoId, String emailUsuario, MultipartFile arquivo) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuario = user.buscarPorEmail(emailUsuario);

        validarAdminOuFundador(grupo, usuario);

        String urlFoto = uploadService.salvarFotoGrupo(grupoId, arquivo);

        grupo.setFotoPerfil(urlFoto);

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse editarGrupo(Long grupoId, String emailUsuario, AtualizarGrupoRequest dto) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuario = user.buscarPorEmail(emailUsuario);

        validarAdminOuFundador(grupo, usuario);

        grupo.setNome(dto.nome());
        grupo.setDescricao(dto.descricao());
        grupo.setFotoPerfil(dto.fotoPerfil());

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public void excluirGrupo(Long grupoId, String emailUsuario) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuario = user.buscarPorEmail(emailUsuario);

        validarFundador(grupo, usuario);

        grupoRep.delete(grupo);
    }
    
    public List<GrupoResponse> listarGrupos() {
        return grupoRep.findAll()
                .stream()
                .map(GrupoResponse::fromEntity)
                .toList();
    }

    public GrupoResponse entrarNoGrupo(Long grupoId, String emailUsuario) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuario = user.buscarPorEmail(emailUsuario);

        if (contemUsuario(grupo.getMembros(), usuario)) {
            throw new RuntimeException("Usuário já está no grupo.");
        }

        grupo.getMembros().add(usuario);

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse sairDoGrupo(Long grupoId, String emailUsuario) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuario = user.buscarPorEmail(emailUsuario);

        if (ehFundador(grupo, usuario)) {
            throw new RuntimeException("O fundador não pode sair do próprio grupo. Exclua o grupo ou transfira a liderança.");
        }

        grupo.getMembros().removeIf(membro -> membro.getId() == usuario.getId());
        grupo.getAdministradores().removeIf(admin -> admin.getId() == usuario.getId());

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse adicionarMembro(Long grupoId, Long usuarioId, String emailUsuarioLogado) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuarioLogado = user.buscarPorEmail(emailUsuarioLogado);

        validarAdminOuFundador(grupo, usuarioLogado);

        Usuario novoMembro = buscarUsuarioPorId(usuarioId);

        if (contemUsuario(grupo.getMembros(), novoMembro)) {
            throw new RuntimeException("Usuário já é membro do grupo.");
        }

        grupo.getMembros().add(novoMembro);

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse removerMembro(Long grupoId, Long usuarioId, String emailUsuarioLogado) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuarioLogado = user.buscarPorEmail(emailUsuarioLogado);

        validarAdminOuFundador(grupo, usuarioLogado);

        Usuario membroRemovido = buscarUsuarioPorId(usuarioId);

        if (ehFundador(grupo, membroRemovido)) {
            throw new RuntimeException("Não é possível remover o fundador do grupo.");
        }

        grupo.getMembros().removeIf(membro -> membro.getId() == membroRemovido.getId());
        grupo.getAdministradores().removeIf(admin -> admin.getId() == membroRemovido.getId());

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse promoverAdministrador(Long grupoId, Long usuarioId, String emailUsuarioLogado) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuarioLogado = user.buscarPorEmail(emailUsuarioLogado);

        validarFundador(grupo, usuarioLogado);

        Usuario novoAdmin = buscarUsuarioPorId(usuarioId);

        if (!contemUsuario(grupo.getMembros(), novoAdmin)) {
            throw new RuntimeException("Usuário precisa ser membro do grupo para virar administrador.");
        }

        if (contemUsuario(grupo.getAdministradores(), novoAdmin)) {
            throw new RuntimeException("Usuário já é administrador.");
        }

        grupo.getAdministradores().add(novoAdmin);

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    public GrupoResponse removerAdministrador(Long grupoId, Long usuarioId, String emailUsuarioLogado) {

        Grupo grupo = buscarGrupoPorId(grupoId);
        Usuario usuarioLogado = user.buscarPorEmail(emailUsuarioLogado);

        validarFundador(grupo, usuarioLogado);

        Usuario adminRemovido = buscarUsuarioPorId(usuarioId);

        if (ehFundador(grupo, adminRemovido)) {
            throw new RuntimeException("Não é possível remover o fundador dos administradores.");
        }

        grupo.getAdministradores().removeIf(admin -> admin.getId() == adminRemovido.getId());

        Grupo grupoSalvo = grupoRep.save(grupo);

        return GrupoResponse.fromEntity(grupoSalvo);
    }

    private Grupo buscarGrupoPorId(Long grupoId) {
        return grupoRep.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));
    }

    private Usuario buscarUsuarioPorId(Long usuarioId) {
        return usuarioRep.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private void validarAdminOuFundador(Grupo grupo, Usuario usuario) {
        if (!ehFundador(grupo, usuario) && !contemUsuario(grupo.getAdministradores(), usuario)) {
            throw new RuntimeException("Você não tem permissão para fazer isso.");
        }
    }

    private void validarFundador(Grupo grupo, Usuario usuario) {
        if (!ehFundador(grupo, usuario)) {
            throw new RuntimeException("Apenas o fundador pode fazer isso.");
        }
    }

    private boolean ehFundador(Grupo grupo, Usuario usuario) {
        return grupo.getFundador() != null && grupo.getFundador().getId() == usuario.getId();
    }

    private boolean contemUsuario(java.util.List<Usuario> lista, Usuario usuario) {
        return lista.stream().anyMatch(u -> u.getId() == usuario.getId());
    }
}
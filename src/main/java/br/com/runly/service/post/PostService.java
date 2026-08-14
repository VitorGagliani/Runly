package br.com.runly.service.post;

import br.com.runly.dto.CriarPostRequest;
import br.com.runly.dto.PostResponse;
import br.com.runly.model.Post;
import br.com.runly.model.Usuario;
import br.com.runly.repository.PostRepository;
import br.com.runly.service.UploadService;
import br.com.runly.service.auth.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioService usuarioService;
    private final UploadService uploadService;

    public PostService(PostRepository postRepository, UsuarioService usuarioService, UploadService uploadService) {
        this.postRepository = postRepository;
        this.usuarioService = usuarioService;
        this.uploadService = uploadService;
    }

    @Transactional
    public PostResponse criar(String emailUsuario, CriarPostRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        Post post = new Post();
        post.setUsuario(usuario);
        post.setTexto(request.texto());
        post.setImagemUrl(request.imagemUrl());

        Post postSalvo = postRepository.save(post);
        return PostResponse.fromEntity(postSalvo);
    }

    @Transactional
    public PostResponse criarComFoto(String emailUsuario, String texto, MultipartFile arquivo) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        String imagemUrl = null;
        if (arquivo != null && !arquivo.isEmpty()) {
            imagemUrl = uploadService.salvarFotoPost(arquivo);
        }

        Post post = new Post();
        post.setUsuario(usuario);
        post.setTexto(texto);
        post.setImagemUrl(imagemUrl);

        Post postSalvo = postRepository.save(post);
        return PostResponse.fromEntity(postSalvo);
    }

    public List<PostResponse> listarFeed() {
        return postRepository.findTop50ByOrderByDataCriacaoDesc()
                .stream()
                .map(PostResponse::fromEntity)
                .toList();
    }

    public List<PostResponse> listarMeusPosts(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);

        return postRepository.findByUsuarioOrderByDataCriacaoDesc(usuario)
                .stream()
                .map(PostResponse::fromEntity)
                .toList();
    }
}

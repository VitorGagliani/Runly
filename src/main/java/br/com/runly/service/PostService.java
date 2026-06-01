package br.com.runly.service;

import br.com.runly.dto.CriarPostRequest;
import br.com.runly.dto.PostResponse;
import br.com.runly.model.Post;
import br.com.runly.model.Usuario;
import br.com.runly.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioService usuarioService;

    public PostService(PostRepository postRepository, UsuarioService usuarioService) {
        this.postRepository = postRepository;
        this.usuarioService = usuarioService;
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

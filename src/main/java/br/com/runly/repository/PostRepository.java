package br.com.runly.repository;

import br.com.runly.model.Post;
import br.com.runly.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findTop50ByOrderByDataCriacaoDesc();
    List<Post> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}

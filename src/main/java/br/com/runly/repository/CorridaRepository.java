package br.com.runly.repository;

import br.com.runly.model.Corrida;
import br.com.runly.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CorridaRepository extends JpaRepository<Corrida, Long> {
    List<Corrida> findByUsuarioOrderByDataCorridaDesc(Usuario usuario);
    
    List<Corrida> findByUsuarioIdAndDataCorridaBetweenOrderByDataCorridaDesc(
    	    Long usuarioId,
    	    LocalDateTime inicio,
    	    LocalDateTime fim
    	);
}

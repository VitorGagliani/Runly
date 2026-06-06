package br.com.runly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.runly.model.grupo.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long>{

}

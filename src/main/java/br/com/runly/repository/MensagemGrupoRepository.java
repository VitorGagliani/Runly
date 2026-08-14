package br.com.runly.repository;

import br.com.runly.model.grupo.MensagemGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensagemGrupoRepository extends JpaRepository<MensagemGrupo, Long> {

    @Query("SELECT m FROM MensagemGrupo m JOIN FETCH m.usuario WHERE m.grupo.id = :grupoId ORDER BY m.dataEnvio ASC")
    List<MensagemGrupo> findByGrupoIdOrderByDataEnvioAsc(@Param("grupoId") Long grupoId);

    List<MensagemGrupo> findTop100ByGrupoIdOrderByDataEnvioDesc(Long grupoId);
}

package br.com.alura.forum.repository;

import br.com.alura.forum.modelo.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    //filtrar pelo nome do relacionamento Topico > Curso.nome
//    List<Topico> findByCurso_Nome(String nomeCurso);
    Page<Topico> findByCurso_Nome(String nomeCurso, Pageable paginacao);
}

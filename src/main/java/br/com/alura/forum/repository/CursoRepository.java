package br.com.alura.forum.repository;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    //filtrar pelo nome do Curso
    Curso findByNome(String nomeCurso);
}

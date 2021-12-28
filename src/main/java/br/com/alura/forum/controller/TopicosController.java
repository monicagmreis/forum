package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    @Cacheable(value = "listaDeTopicos")
    public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso,
                                 @PageableDefault(sort = "id",
                                         direction = Sort.Direction.DESC,
                                 page = 0, size = 10) Pageable paginacao) {
//                                     @RequestParam int pagina,
//                                     @RequestParam int qtd,
//                                 @RequestParam String ordenacao){

        //se não mandar os parâmetros na URL.. Ele usa o PageableDefault

        //Pageable paginacao = PageRequest.of(pagina, qtd, Sort.Direction.DESC, ordenacao);

        if (nomeCurso == null) {
            Page<Topico> topicos = topicoRepository.findAll(paginacao);
            return TopicoDto.converter(topicos);
        } else {
            Page<Topico> topico = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
            return TopicoDto.converter(topico);
        }
    }


//    @GetMapping
//    public List<TopicoDto> listaOld(String nomeCurso){
//        Topico topico = new Topico("Dúvida", "Dúvida com Spring",
//                new Curso("Spring", "Programação"));
//
//        return TopicoDto.converter(Arrays.asList(topico, topico, topico));

//        if (nomeCurso == null) {
//            List<Topico> topicos = topicoRepository.findAll();
//            return TopicoDto.converter(topicos);
//        } else {
//            List<Topico> topico = topicoRepository.findByCurso_Nome(nomeCurso);
//            return TopicoDto.converter(topico);
//        }
//    }

    //TopicoDto > Dados que saem da API direto para o Cliente
    //TopicoForm > Dados que chegam do Cliente para API

    @PostMapping
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm topicoForm, UriComponentsBuilder uriBuilder) {
        //Para testar Método POST, precisamos de Ferramentas
        //Ex: Postman

        Topico topico = topicoForm.converter(cursoRepository);
        topicoRepository.save(topico);

        //Devolver Código 201, com Calendário Location
        // e um Corpo da Resposta.. com uma Representação do Recurso Cadastrado
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
//        Topico topico = topicoRepository.getOne(id);
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isPresent()) {
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
        }

        return ResponseEntity.notFound().build();

    }

    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id,
                                               @RequestBody @Valid AtualizacaoTopicoForm topicoForm){
//        //////aqui as informacoes ja foram gravadas no banco de dados
//        Topico topico = topicoForm.atualizar(id, topicoRepository);
//
//        //////esse eh o corpo que vai ser devolvido na resposta do servidor
//        return ResponseEntity.ok(new TopicoDto(topico));

        Optional<Topico> optional = topicoRepository.findById(id);
        if (optional.isPresent()) {
            Topico topico = topicoForm.atualizar(id, topicoRepository);
            return ResponseEntity.ok(new TopicoDto(topico));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<?> remover(@PathVariable Long id){
//        topicoRepository.deleteById(id);
//        return ResponseEntity.ok().build();
        Optional<Topico> optional = topicoRepository.findById(id);
        if (optional.isPresent()) {
            topicoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}

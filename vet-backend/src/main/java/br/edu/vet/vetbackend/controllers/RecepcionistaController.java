package br.edu.vet.vetbackend.controllers;

import br.edu.vet.vetbackend.dto.RecepcionistaRequest;
import br.edu.vet.vetbackend.dto.RecepcionistaResponse;

import br.edu.vet.vetbackend.servicos.RecepcionistaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recepcionistas")
public class RecepcionistaController {

    private final RecepcionistaService service;

    public RecepcionistaController(RecepcionistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RecepcionistaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RecepcionistaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<RecepcionistaResponse> cadastrar(@Valid @RequestBody RecepcionistaRequest req) {
        RecepcionistaResponse criado = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/api/recepcionistas/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public RecepcionistaResponse atualizar(@PathVariable Long id, @Valid @RequestBody RecepcionistaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

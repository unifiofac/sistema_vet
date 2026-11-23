package br.edu.vet.vetbackend.controllers;

import br.edu.vet.vetbackend.dto.VeterinarioRequest;
import br.edu.vet.vetbackend.dto.VeterinarioResponse;

import br.edu.vet.vetbackend.servicos.VeterinarioService;
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
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService service;

    public VeterinarioController(VeterinarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<VeterinarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public VeterinarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponse> cadastrar(@Valid @RequestBody VeterinarioRequest req) {
        VeterinarioResponse criado = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public VeterinarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

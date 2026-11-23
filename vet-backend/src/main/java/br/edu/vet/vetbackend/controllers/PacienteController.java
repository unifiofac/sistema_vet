package br.edu.vet.vetbackend.controllers;

import br.edu.vet.vetbackend.dto.PacienteRequest;
import br.edu.vet.vetbackend.dto.PacienteResponse;

import br.edu.vet.vetbackend.servicos.PacienteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PacienteResponse> listar(@RequestParam(required = false) Long donoId,
                                         @RequestParam(required = false) String nome) {
        return service.listar(donoId, nome);
    }

    @GetMapping("/meus")
    public List<PacienteResponse> meus(Authentication auth) {
        return service.listar(Long.valueOf(auth.getName()), null);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> cadastrar(@Valid @RequestBody PacienteRequest req) {
        PacienteResponse criado = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/api/pacientes/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public PacienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

package br.edu.vet.vetbackend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// usado pela hospedagem para saber se o servidor está de pé
@RestController
public class HealthController {

    public record Status(String status) {
    }

    @GetMapping("/api/health")
    public Status health() {
        return new Status("ok");
    }
}

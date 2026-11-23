package br.edu.vet.vetbackend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    public record Status(String status) {
    }

    @GetMapping("/api/health")
    public Status health() {
        return new Status("ok");
    }
}

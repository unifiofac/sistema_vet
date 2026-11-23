package br.edu.vet.vetbackend.controllers;

import br.edu.vet.vetbackend.dto.LoginRequest;
import br.edu.vet.vetbackend.dto.UsuarioResponse;
import br.edu.vet.vetbackend.seguranca.JwtService;
import br.edu.vet.vetbackend.servicos.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final JwtService jwt;

    public AuthController(AuthService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    public record LoginResponse(String token, UsuarioResponse usuario) {
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        UsuarioResponse usuario = service.login(req);
        return new LoginResponse(jwt.gerar(usuario.id(), usuario.tipo()), usuario);
    }
}

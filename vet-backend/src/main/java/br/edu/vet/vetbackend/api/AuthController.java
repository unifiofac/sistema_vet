package br.edu.vet.vetbackend.api;

import br.edu.vet.vetbackend.entidades.Usuario;
import br.edu.vet.vetbackend.repositorios.UsuarioRepository;
import br.edu.vet.vetbackend.seguranca.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    // hash usado quando o e-mail não existe, para o tempo de resposta não revelar quais e-mails são válidos
    private final String hashFalso;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
        this.hashFalso = encoder.encode("senha-que-ninguem-usa");
    }

    public record LoginRequest(@NotBlank @Size(max = 150) String email, @NotBlank @Size(max = 200) String senha) {
    }

    public record LoginResponse(String token, UsuarioDto usuario) {
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(req.email().trim()).orElse(null);
        boolean ok = encoder.matches(req.senha(), usuario != null ? usuario.getSenhaHash() : hashFalso);

        if (usuario == null || !ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
        }
        return new LoginResponse(jwt.gerar(usuario), UsuarioDto.de(usuario));
    }
}

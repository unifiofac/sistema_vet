package br.edu.vet.vetbackend.api;

import br.edu.vet.vetbackend.entidades.TipoUsuario;
import br.edu.vet.vetbackend.entidades.Usuario;
import br.edu.vet.vetbackend.repositorios.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Rotas exclusivas do veterinário (ver SecurityConfig)
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    public record NovoClienteRequest(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Email @Size(max = 150) String email,
            @Size(max = 30) String telefone,
            @NotBlank @Size(min = 8, max = 100) String senha) {
    }

    @GetMapping("/clientes")
    public List<UsuarioDto> listarClientes() {
        return usuarios.findByTipoOrderByNomeAsc(TipoUsuario.CLIENTE).stream().map(UsuarioDto::de).toList();
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto criarCliente(@Valid @RequestBody NovoClienteRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail.");
        }
        Usuario novo = new Usuario(req.nome().trim(), email, req.telefone(),
                encoder.encode(req.senha()), TipoUsuario.CLIENTE);
        return UsuarioDto.de(usuarios.save(novo));
    }
}

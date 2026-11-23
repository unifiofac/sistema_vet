package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.dto.LoginRequest;
import br.edu.vet.vetbackend.dto.UsuarioResponse;
import br.edu.vet.vetbackend.excecoes.CredenciaisInvalidasException;
import br.edu.vet.vetbackend.repositorios.ClienteRepository;
import br.edu.vet.vetbackend.repositorios.RecepcionistaRepository;
import br.edu.vet.vetbackend.repositorios.VeterinarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Login simples: procura o e-mail em cada tipo de usuário e confere a senha com BCrypt
@Service
public class AuthService {

    private final VeterinarioRepository veterinarios;
    private final ClienteRepository clientes;
    private final RecepcionistaRepository recepcionistas;
    private final PasswordEncoder encoder;

    public AuthService(VeterinarioRepository veterinarios, ClienteRepository clientes,
                       RecepcionistaRepository recepcionistas, PasswordEncoder encoder) {
        this.veterinarios = veterinarios;
        this.clientes = clientes;
        this.recepcionistas = recepcionistas;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest req) {
        String email = req.email().trim();

        var vet = veterinarios.findByEmailIgnoreCase(email);
        if (vet.isPresent() && encoder.matches(req.senha(), vet.get().getSenha())) {
            var v = vet.get();
            return new UsuarioResponse(v.getId(), v.getNome(), v.getEmail(), v.getTelefone(), "veterinario");
        }

        var cliente = clientes.findByEmailIgnoreCase(email);
        if (cliente.isPresent() && encoder.matches(req.senha(), cliente.get().getSenha())) {
            var c = cliente.get();
            return new UsuarioResponse(c.getId(), c.getNome(), c.getEmail(), c.getTelefone(), "cliente");
        }

        var recep = recepcionistas.findByEmailIgnoreCase(email);
        if (recep.isPresent() && encoder.matches(req.senha(), recep.get().getSenha())) {
            var r = recep.get();
            return new UsuarioResponse(r.getId(), r.getNome(), r.getEmail(), r.getTelefone(), "recepcionista");
        }

        // mesma mensagem para e-mail inexistente e senha errada
        throw new CredenciaisInvalidasException();
    }
}

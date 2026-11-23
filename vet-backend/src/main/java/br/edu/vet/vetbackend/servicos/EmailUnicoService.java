package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.excecoes.RegraNegocioException;
import br.edu.vet.vetbackend.repositorios.ClienteRepository;
import br.edu.vet.vetbackend.repositorios.RecepcionistaRepository;
import br.edu.vet.vetbackend.repositorios.VeterinarioRepository;
import org.springframework.stereotype.Service;

// O login procura o e-mail nas três tabelas, então ele não pode se repetir entre elas
@Service
public class EmailUnicoService {

    private final ClienteRepository clientes;
    private final VeterinarioRepository veterinarios;
    private final RecepcionistaRepository recepcionistas;

    public EmailUnicoService(ClienteRepository clientes, VeterinarioRepository veterinarios,
                             RecepcionistaRepository recepcionistas) {
        this.clientes = clientes;
        this.veterinarios = veterinarios;
        this.recepcionistas = recepcionistas;
    }

    public void garantirDisponivel(String email) {
        if (clientes.existsByEmailIgnoreCase(email)
                || veterinarios.existsByEmailIgnoreCase(email)
                || recepcionistas.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail " + email + ".");
        }
    }
}

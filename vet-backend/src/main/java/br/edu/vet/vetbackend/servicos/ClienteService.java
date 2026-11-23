package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.dto.ClienteRequest;
import br.edu.vet.vetbackend.dto.ClienteResponse;
import br.edu.vet.vetbackend.dto.PacienteResponse;
import br.edu.vet.vetbackend.entidades.Cliente;
import br.edu.vet.vetbackend.excecoes.RecursoNaoEncontradoException;
import br.edu.vet.vetbackend.excecoes.RegraNegocioException;
import br.edu.vet.vetbackend.repositorios.ClienteRepository;
import br.edu.vet.vetbackend.repositorios.PacienteRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PacienteRepository pacientes;
    private final EmailUnicoService emailUnico;
    private final PasswordEncoder encoder;

    public ClienteService(ClienteRepository repository, PacienteRepository pacientes,
                          EmailUnicoService emailUnico, PasswordEncoder encoder) {
        this.repository = repository;
        this.pacientes = pacientes;
        this.emailUnico = emailUnico;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String nome) {
        List<Cliente> lista = (nome == null || nome.isBlank())
                ? repository.findAll(Sort.by("nome"))
                : repository.findByNomeContainingIgnoreCaseOrderByNome(nome.trim());
        return lista.stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return ClienteResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listarPacientes(Long id) {
        buscarEntidade(id);
        return pacientes.findByDonoIdOrderByNomeAsc(id).stream().map(PacienteResponse::de).toList();
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest req) {
        if (req.senha() == null || req.senha().isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória no cadastro.");
        }
        emailUnico.garantirDisponivel(req.email());

        Cliente c = new Cliente();
        copiar(req, c);
        c.setSenha(encoder.encode(req.senha()));
        return ClienteResponse.de(repository.save(c));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente c = buscarEntidade(id);
        if (!c.getEmail().equalsIgnoreCase(req.email())) {
            emailUnico.garantirDisponivel(req.email());
        }
        copiar(req, c);
        if (req.senha() != null && !req.senha().isBlank()) {
            c.setSenha(encoder.encode(req.senha()));
        }
        return ClienteResponse.de(c);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente c = buscarEntidade(id);
        if (pacientes.existsByDonoId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o cliente ainda tem pacientes cadastrados.");
        }
        repository.delete(c);
    }

    Cliente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " não encontrado."));
    }

    private void copiar(ClienteRequest req, Cliente c) {
        c.setNome(req.nome().trim());
        c.setEmail(req.email().trim().toLowerCase());
        c.setTelefone(req.telefone());
        c.setCpf(req.cpf());
        c.setEndereco(req.endereco());
    }
}

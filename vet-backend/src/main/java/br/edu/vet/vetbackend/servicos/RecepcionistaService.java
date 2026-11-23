package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.dto.RecepcionistaRequest;
import br.edu.vet.vetbackend.dto.RecepcionistaResponse;
import br.edu.vet.vetbackend.entidades.Recepcionista;
import br.edu.vet.vetbackend.excecoes.RecursoNaoEncontradoException;
import br.edu.vet.vetbackend.excecoes.RegraNegocioException;
import br.edu.vet.vetbackend.repositorios.RecepcionistaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository repository;
    private final EmailUnicoService emailUnico;
    private final PasswordEncoder encoder;

    public RecepcionistaService(RecepcionistaRepository repository, EmailUnicoService emailUnico,
                                PasswordEncoder encoder) {
        this.repository = repository;
        this.emailUnico = emailUnico;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<RecepcionistaResponse> listar() {
        return repository.findAll(Sort.by("nome")).stream().map(RecepcionistaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public RecepcionistaResponse buscar(Long id) {
        return RecepcionistaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public RecepcionistaResponse cadastrar(RecepcionistaRequest req) {
        if (req.senha() == null || req.senha().isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória no cadastro.");
        }
        emailUnico.garantirDisponivel(req.email());

        Recepcionista r = new Recepcionista();
        copiar(req, r);
        r.setSenha(encoder.encode(req.senha()));
        return RecepcionistaResponse.de(repository.save(r));
    }

    @Transactional
    public RecepcionistaResponse atualizar(Long id, RecepcionistaRequest req) {
        Recepcionista r = buscarEntidade(id);
        if (!r.getEmail().equalsIgnoreCase(req.email())) {
            emailUnico.garantirDisponivel(req.email());
        }
        copiar(req, r);
        if (req.senha() != null && !req.senha().isBlank()) {
            r.setSenha(encoder.encode(req.senha()));
        }
        return RecepcionistaResponse.de(r);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Recepcionista buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recepcionista " + id + " não encontrado(a)."));
    }

    private void copiar(RecepcionistaRequest req, Recepcionista r) {
        r.setNome(req.nome().trim());
        r.setEmail(req.email().trim().toLowerCase());
        r.setTelefone(req.telefone());
        r.setTurno(req.turno());
    }
}

package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.dto.AgendamentoRequest;
import br.edu.vet.vetbackend.dto.AgendamentoResponse;
import br.edu.vet.vetbackend.entidades.Agendamento;
import br.edu.vet.vetbackend.excecoes.RecursoNaoEncontradoException;
import br.edu.vet.vetbackend.excecoes.RegraNegocioException;
import br.edu.vet.vetbackend.repositorios.AgendamentoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final PacienteService pacienteService;
    private final VeterinarioService veterinarioService;

    public AgendamentoService(AgendamentoRepository repository, PacienteService pacienteService,
                              VeterinarioService veterinarioService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
        this.veterinarioService = veterinarioService;
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listar() {
        return repository.findAllByOrderByDataHoraAsc().stream().map(AgendamentoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoResponse buscar(Long id) {
        return AgendamentoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoRequest req) {
        String status = status(req);
        if (!"CANCELADO".equals(status)) {
            if (repository.existsByVeterinarioIdAndDataHoraAndStatusNot(req.veterinarioId(), req.dataHora(), "CANCELADO")) {
                throw conflito();
            }
        }
        Agendamento a = new Agendamento();
        copiar(req, a);
        return AgendamentoResponse.de(repository.save(a));
    }

    @Transactional
    public AgendamentoResponse atualizar(Long id, AgendamentoRequest req) {
        Agendamento a = buscarEntidade(id);
        if (!"CANCELADO".equals(status(req))
                && repository.existsByVeterinarioIdAndDataHoraAndStatusNotAndIdNot(
                        req.veterinarioId(), req.dataHora(), "CANCELADO", id)) {
            throw conflito();
        }
        copiar(req, a);
        return AgendamentoResponse.de(a);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Agendamento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento " + id + " não encontrado."));
    }

    private RegraNegocioException conflito() {
        return new RegraNegocioException("Esse veterinário já tem um atendimento marcado neste horário.");
    }

    private String status(AgendamentoRequest req) {
        return req.status() == null || req.status().isBlank() ? "AGENDADO" : req.status();
    }

    private void copiar(AgendamentoRequest req, Agendamento a) {
        a.setDataHora(req.dataHora());
        a.setMotivo(req.motivo().trim());
        a.setStatus(status(req));
        a.setPaciente(pacienteService.buscarEntidade(req.pacienteId()));
        a.setVeterinario(veterinarioService.buscarEntidade(req.veterinarioId()));
    }
}

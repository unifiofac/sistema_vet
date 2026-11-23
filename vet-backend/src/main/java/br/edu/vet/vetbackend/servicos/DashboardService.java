package br.edu.vet.vetbackend.servicos;

import br.edu.vet.vetbackend.dto.AgendamentoResponse;
import br.edu.vet.vetbackend.dto.DashboardResponse;
import br.edu.vet.vetbackend.repositorios.AgendamentoRepository;
import br.edu.vet.vetbackend.repositorios.ClienteRepository;
import br.edu.vet.vetbackend.repositorios.PacienteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final PacienteRepository pacientes;
    private final ClienteRepository clientes;
    private final AgendamentoRepository agendamentos;

    public DashboardService(PacienteRepository pacientes, ClienteRepository clientes,
                            AgendamentoRepository agendamentos) {
        this.pacientes = pacientes;
        this.clientes = clientes;
        this.agendamentos = agendamentos;
    }

    @Transactional(readOnly = true)
    public DashboardResponse resumo() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();

        Map<String, Long> porEspecie = new LinkedHashMap<>();
        for (Object[] linha : pacientes.contarPorEspecie()) {
            porEspecie.put((String) linha[0], (Long) linha[1]);
        }

        List<AgendamentoResponse> proximos = agendamentos
                .findByStatusAndDataHoraGreaterThanEqualOrderByDataHoraAsc("AGENDADO", agora).stream()
                .limit(5)
                .map(AgendamentoResponse::de)
                .toList();

        return new DashboardResponse(
                pacientes.count(),
                clientes.count(),
                agendamentos.countByDataHoraBetweenAndStatusNot(
                        hoje.atStartOfDay(), hoje.plusDays(1).atStartOfDay(), "CANCELADO"),
                agendamentos.countByStatusAndDataHoraGreaterThanEqual("AGENDADO", agora),
                porEspecie,
                proximos);
    }
}

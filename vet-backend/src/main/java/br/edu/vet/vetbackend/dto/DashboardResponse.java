package br.edu.vet.vetbackend.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalPacientes,
        long totalClientes,
        long agendamentosHoje,
        long agendamentosPendentes,
        Map<String, Long> pacientesPorEspecie,
        List<AgendamentoResponse> proximosAgendamentos) {
}

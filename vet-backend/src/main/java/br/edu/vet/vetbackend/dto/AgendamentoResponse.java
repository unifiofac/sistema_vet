package br.edu.vet.vetbackend.dto;

import br.edu.vet.vetbackend.entidades.Agendamento;
import java.time.LocalDateTime;

public record AgendamentoResponse(
        Long id,
        LocalDateTime dataHora,
        String motivo,
        String status,
        Long pacienteId,
        String nomePaciente,
        Long veterinarioId,
        String nomeVeterinario) {

    public static AgendamentoResponse de(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(), a.getDataHora(), a.getMotivo(), a.getStatus(),
                a.getPaciente().getId(), a.getPaciente().getNome(),
                a.getVeterinario().getId(), a.getVeterinario().getNome());
    }
}

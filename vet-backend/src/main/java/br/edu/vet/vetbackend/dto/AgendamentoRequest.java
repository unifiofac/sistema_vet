package br.edu.vet.vetbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AgendamentoRequest(
        @NotNull(message = "Data e hora são obrigatórias") LocalDateTime dataHora,
        @NotBlank(message = "Motivo é obrigatório") @Size(max = 200) String motivo,
        @Pattern(regexp = "AGENDADO|CONCLUIDO|CANCELADO", message = "Status inválido") String status,
        @NotNull(message = "Paciente é obrigatório") Long pacienteId,
        @NotNull(message = "Veterinário é obrigatório") Long veterinarioId) {
}

package br.edu.vet.vetbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PacienteRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
        @NotBlank(message = "Espécie é obrigatória") @Size(max = 50) String especie,
        @Size(max = 50) String raca,
        @NotNull(message = "Idade é obrigatória") @PositiveOrZero Integer idade,
        @NotNull(message = "Peso é obrigatório") @Positive Double peso,
        @NotNull(message = "Dono (donoId) é obrigatório") Long donoId,
        @Size(max = 2000) String historicoMedico,
        @Size(max = 1000) String alergias,
        @Size(max = 1000) String medicamentosAtuais) {
}

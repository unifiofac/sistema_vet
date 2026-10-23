package br.edu.vet.vetbackend.ia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record IaRequest(
        @NotBlank @Size(max = 2000) String pergunta,
        @NotNull @Valid PacienteContexto paciente,
        @Size(max = 20) List<@Valid Mensagem> historico) {

    public record PacienteContexto(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Size(max = 50) String especie,
            @Size(max = 50) String raca,
            Integer idade,
            Double peso,
            @Size(max = 2000) String historicoMedico,
            @Size(max = 1000) String alergias,
            @Size(max = 1000) String medicamentosAtuais) {
    }

    // papel: "user" (veterinário) ou "model" (IA)
    public record Mensagem(
            @NotBlank String papel,
            @NotBlank @Size(max = 4000) String texto) {
    }
}

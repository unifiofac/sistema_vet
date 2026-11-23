package br.edu.vet.vetbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecepcionistaRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 100) String email,
        @Size(max = 20) String telefone,
        @Size(max = 20) String turno,
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha) {
}

package br.edu.vet.vetbackend.excecoes;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String mensagem,
        Map<String, String> campos) {
}

package br.edu.vet.vetbackend.dto;

import br.edu.vet.vetbackend.entidades.Recepcionista;

public record RecepcionistaResponse(Long id, String nome, String email, String telefone, String turno) {

    public static RecepcionistaResponse de(Recepcionista r) {
        return new RecepcionistaResponse(r.getId(), r.getNome(), r.getEmail(), r.getTelefone(), r.getTurno());
    }
}

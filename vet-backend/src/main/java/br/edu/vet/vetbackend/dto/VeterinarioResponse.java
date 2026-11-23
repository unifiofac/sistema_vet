package br.edu.vet.vetbackend.dto;

import br.edu.vet.vetbackend.entidades.Veterinario;

public record VeterinarioResponse(Long id, String nome, String email, String telefone, String crmv, String especialidade) {

    public static VeterinarioResponse de(Veterinario v) {
        return new VeterinarioResponse(v.getId(), v.getNome(), v.getEmail(), v.getTelefone(), v.getCrmv(), v.getEspecialidade());
    }
}

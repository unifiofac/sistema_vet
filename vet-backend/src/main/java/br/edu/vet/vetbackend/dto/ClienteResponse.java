package br.edu.vet.vetbackend.dto;

import br.edu.vet.vetbackend.entidades.Cliente;

public record ClienteResponse(Long id, String nome, String email, String telefone, String cpf, String endereco) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getTelefone(), c.getCpf(), c.getEndereco());
    }
}

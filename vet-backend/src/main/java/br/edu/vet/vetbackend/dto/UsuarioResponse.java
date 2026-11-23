package br.edu.vet.vetbackend.dto;

// Mesmo formato do model Usuario do Angular. tipo: cliente | veterinario | recepcionista
public record UsuarioResponse(Long id, String nome, String email, String telefone, String tipo) {
}

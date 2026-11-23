package br.edu.vet.vetbackend.api;

import br.edu.vet.vetbackend.entidades.Usuario;

// "tipo" sai em minúsculas ("veterinario" | "cliente"), como o frontend espera. Nunca expõe a senha.
public record UsuarioDto(Long id, String nome, String email, String telefone, String tipo) {

    public static UsuarioDto de(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(),
                u.getTipo().name().toLowerCase());
    }
}

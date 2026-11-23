package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.TipoUsuario;
import br.edu.vet.vetbackend.entidades.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByTipoOrderByNomeAsc(TipoUsuario tipo);
}

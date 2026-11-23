package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Veterinario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCrmvIgnoreCase(String crmv);
}

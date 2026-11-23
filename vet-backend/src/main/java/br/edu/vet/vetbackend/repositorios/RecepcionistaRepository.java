package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Recepcionista;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long> {

    Optional<Recepcionista> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}

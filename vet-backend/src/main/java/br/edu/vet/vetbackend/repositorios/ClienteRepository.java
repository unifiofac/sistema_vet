package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Cliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Cliente> findByNomeContainingIgnoreCaseOrderByNome(String nome);
}

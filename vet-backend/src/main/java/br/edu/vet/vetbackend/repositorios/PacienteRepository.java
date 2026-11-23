package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Paciente;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // carrega o dono junto (open-in-view está desligado)
    @EntityGraph(attributePaths = "dono")
    List<Paciente> findAllByOrderByNomeAsc();

    @EntityGraph(attributePaths = "dono")
    List<Paciente> findByDonoIdOrderByNomeAsc(Long donoId);
}

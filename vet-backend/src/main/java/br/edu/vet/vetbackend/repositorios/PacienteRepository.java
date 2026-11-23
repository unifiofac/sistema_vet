package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Paciente;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // já traz o dono junto, para não fazer uma consulta extra por paciente
    @EntityGraph(attributePaths = "dono")
    List<Paciente> findAllByOrderByNomeAsc();

    @EntityGraph(attributePaths = "dono")
    List<Paciente> findByDonoIdOrderByNomeAsc(Long donoId);

    @EntityGraph(attributePaths = "dono")
    List<Paciente> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    boolean existsByDonoId(Long donoId);

    @Query("select p.especie, count(p) from Paciente p group by p.especie order by count(p) desc")
    List<Object[]> contarPorEspecie();
}

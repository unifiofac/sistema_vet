package br.edu.vet.vetbackend.repositorios;

import br.edu.vet.vetbackend.entidades.Agendamento;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @EntityGraph(attributePaths = {"paciente", "veterinario"})
    List<Agendamento> findAllByOrderByDataHoraAsc();

    @EntityGraph(attributePaths = {"paciente", "veterinario"})
    List<Agendamento> findByStatusAndDataHoraGreaterThanEqualOrderByDataHoraAsc(String status, LocalDateTime aPartirDe);

    boolean existsByVeterinarioIdAndDataHoraAndStatusNot(Long veterinarioId, LocalDateTime dataHora, String status);

    boolean existsByVeterinarioIdAndDataHoraAndStatusNotAndIdNot(Long veterinarioId, LocalDateTime dataHora, String status, Long id);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByVeterinarioId(Long veterinarioId);

    long countByDataHoraBetweenAndStatusNot(LocalDateTime inicio, LocalDateTime fim, String status);

    long countByStatusAndDataHoraGreaterThanEqual(String status, LocalDateTime aPartirDe);
}

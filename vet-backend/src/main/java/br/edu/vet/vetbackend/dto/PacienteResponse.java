package br.edu.vet.vetbackend.dto;

import br.edu.vet.vetbackend.entidades.Paciente;
import java.time.LocalDate;

// Mesmo formato do model Paciente do Angular
public record PacienteResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        Double peso,
        Long donoId,
        String nomeDono,
        String historicoMedico,
        String alergias,
        String medicamentosAtuais,
        LocalDate dataCadastro) {

    public static PacienteResponse de(Paciente p) {
        return new PacienteResponse(
                p.getId(), p.getNome(), p.getEspecie(), p.getRaca(), p.getIdade(), p.getPeso(),
                p.getDono().getId(), p.getDono().getNome(),
                p.getHistoricoMedico(), p.getAlergias(), p.getMedicamentosAtuais(),
                p.getDataCadastro());
    }
}

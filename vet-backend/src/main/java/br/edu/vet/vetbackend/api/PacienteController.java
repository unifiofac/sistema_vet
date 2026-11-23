package br.edu.vet.vetbackend.api;

import br.edu.vet.vetbackend.entidades.Paciente;
import br.edu.vet.vetbackend.entidades.TipoUsuario;
import br.edu.vet.vetbackend.entidades.Usuario;
import br.edu.vet.vetbackend.repositorios.PacienteRepository;
import br.edu.vet.vetbackend.repositorios.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository pacientes;
    private final UsuarioRepository usuarios;

    public PacienteController(PacienteRepository pacientes, UsuarioRepository usuarios) {
        this.pacientes = pacientes;
        this.usuarios = usuarios;
    }

    public record NovoPacienteRequest(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Size(max = 50) String especie,
            @Size(max = 50) String raca,
            @NotNull @PositiveOrZero Integer idade,
            @NotNull @PositiveOrZero Double peso,
            @NotNull Long donoId,
            @Size(max = 2000) String historicoMedico,
            @Size(max = 1000) String alergias,
            @Size(max = 1000) String medicamentosAtuais) {
    }

    // formato igual ao do modelo Paciente do Angular
    public record PacienteDto(Long id, String nome, String especie, String raca, Integer idade, Double peso,
                              Long donoId, String nomeDono, String historicoMedico, String alergias,
                              String medicamentosAtuais, String dataCadastro) {

        static PacienteDto de(Paciente p) {
            return new PacienteDto(p.getId(), p.getNome(), p.getEspecie(), p.getRaca(), p.getIdade(), p.getPeso(),
                    p.getDono().getId(), p.getDono().getNome(), p.getHistoricoMedico(), p.getAlergias(),
                    p.getMedicamentosAtuais(), p.getDataCadastro().toString());
        }
    }

    // veterinário: todos os pacientes
    @GetMapping
    public List<PacienteDto> listarTodos() {
        return pacientes.findAllByOrderByNomeAsc().stream().map(PacienteDto::de).toList();
    }

    // cliente: somente os próprios pets (o id vem do token, nunca do cliente)
    @GetMapping("/meus")
    public List<PacienteDto> listarMeus(@AuthenticationPrincipal Jwt jwt) {
        Long donoId = Long.valueOf(jwt.getSubject());
        return pacientes.findByDonoIdOrderByNomeAsc(donoId).stream().map(PacienteDto::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteDto cadastrar(@Valid @RequestBody NovoPacienteRequest req) {
        Usuario dono = usuarios.findById(req.donoId())
                .filter(u -> u.getTipo() == TipoUsuario.CLIENTE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dono não encontrado."));

        Paciente salvo = pacientes.save(new Paciente(
                req.nome().trim(), req.especie().trim(), vazioParaNulo(req.raca()), req.idade(), req.peso(), dono,
                vazioParaNulo(req.historicoMedico()), vazioParaNulo(req.alergias()),
                vazioParaNulo(req.medicamentosAtuais())));
        return PacienteDto.de(salvo);
    }

    private static String vazioParaNulo(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}

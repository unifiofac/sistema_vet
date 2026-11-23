package br.edu.vet.vetbackend;

import br.edu.vet.vetbackend.entidades.Agendamento;
import br.edu.vet.vetbackend.entidades.Cliente;
import br.edu.vet.vetbackend.entidades.Paciente;
import br.edu.vet.vetbackend.entidades.Recepcionista;
import br.edu.vet.vetbackend.entidades.Veterinario;
import br.edu.vet.vetbackend.repositorios.AgendamentoRepository;
import br.edu.vet.vetbackend.repositorios.ClienteRepository;
import br.edu.vet.vetbackend.repositorios.PacienteRepository;
import br.edu.vet.vetbackend.repositorios.RecepcionistaRepository;
import br.edu.vet.vetbackend.repositorios.VeterinarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final VeterinarioRepository veterinarios;
    private final ClienteRepository clientes;
    private final RecepcionistaRepository recepcionistas;
    private final PacienteRepository pacientes;
    private final AgendamentoRepository agendamentos;
    private final PasswordEncoder encoder;

    @Value("${app.admin.nome}") private String adminNome;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.senha}") private String adminSenha;
    @Value("${app.admin.crmv}") private String adminCrmv;
    @Value("${app.seed.demo}") private boolean seedDemo;
    @Value("${app.seed.senha}") private String seedSenha;

    public DadosIniciais(VeterinarioRepository veterinarios, ClienteRepository clientes,
                         RecepcionistaRepository recepcionistas, PacienteRepository pacientes,
                         AgendamentoRepository agendamentos, PasswordEncoder encoder) {
        this.veterinarios = veterinarios;
        this.clientes = clientes;
        this.recepcionistas = recepcionistas;
        this.pacientes = pacientes;
        this.agendamentos = agendamentos;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (veterinarios.count() + clientes.count() + recepcionistas.count() > 0) return;

        if (seedDemo) {
            if (seedSenha.length() < 8) {
                log.warn("SEED_DEMO=true, mas SEED_SENHA precisa ter pelo menos 8 caracteres. Nada foi criado.");
                return;
            }
            criarDemo();
            return;
        }

        if (!adminEmail.isBlank() && adminSenha.length() >= 8) {
            veterinarios.save(veterinario(adminNome, adminEmail.trim().toLowerCase(), null, adminCrmv, encoder.encode(adminSenha)));
            log.info("Primeiro veterinário criado: {}", adminEmail);
        } else {
            log.warn("Banco vazio e sem ADMIN_EMAIL/ADMIN_SENHA (mín. 8 caracteres): ninguém conseguirá entrar.");
        }
    }

    private void criarDemo() {
        String hash = encoder.encode(seedSenha);
        Veterinario carlos = veterinarios.save(veterinario("Dr. Carlos Silva", "carlos@vetclinic.com", "(11) 98765-4321", "CRMV-SP 12345", hash));

        Recepcionista rec = new Recepcionista();
        rec.setNome("Ana Recepção");
        rec.setEmail("ana@vetclinic.com");
        rec.setTurno("Manhã");
        rec.setSenha(hash);
        recepcionistas.save(rec);

        Cliente maria = cliente("Maria Santos", "maria@email.com", "(11) 91234-5678", hash);
        Cliente joao = cliente("João Oliveira", "joao@email.com", "(11) 99876-5432", hash);

        Paciente rex = pacientes.save(paciente("Rex", "Cachorro", "Labrador", 5, 30.5, maria,
                "Vacinação em dia. Castrado em 2021.", "Nenhuma alergia conhecida"));
        Paciente mimi = pacientes.save(paciente("Mimi", "Gato", "Persa", 3, 4.2, maria,
                "Vacinação em dia.", "Alérgica a frango"));
        pacientes.save(paciente("Thor", "Cachorro", "Pastor Alemão", 7, 35.0, joao,
                "Displasia coxofemoral leve.", "Nenhuma alergia conhecida"));
        agendamentos.save(agendamento(rex, carlos, "Consulta de rotina", 0, 14));
        agendamentos.save(agendamento(mimi, carlos, "Vacina anual", 1, 9));
        log.info("Dados de demonstração criados.");
    }

    private Agendamento agendamento(Paciente p, Veterinario v, String motivo, int diasAFrente, int hora) {
        Agendamento a = new Agendamento();
        a.setPaciente(p);
        a.setVeterinario(v);
        a.setMotivo(motivo);
        a.setDataHora(LocalDate.now().plusDays(diasAFrente).atTime(LocalTime.of(hora, 0)));
        return a;
    }

    private Veterinario veterinario(String nome, String email, String telefone, String crmv, String hash) {
        Veterinario v = new Veterinario();
        v.setNome(nome);
        v.setEmail(email);
        v.setTelefone(telefone);
        v.setCrmv(crmv);
        v.setSenha(hash);
        return v;
    }

    private Cliente cliente(String nome, String email, String telefone, String hash) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(telefone);
        c.setSenha(hash);
        return clientes.save(c);
    }

    private Paciente paciente(String nome, String especie, String raca, int idade, double peso,
                              Cliente dono, String historico, String alergias) {
        Paciente p = new Paciente();
        p.setNome(nome);
        p.setEspecie(especie);
        p.setRaca(raca);
        p.setIdade(idade);
        p.setPeso(peso);
        p.setDono(dono);
        p.setHistoricoMedico(historico);
        p.setAlergias(alergias);
        return p;
    }
}

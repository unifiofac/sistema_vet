package br.edu.vet.vetbackend;

import br.edu.vet.vetbackend.entidades.Paciente;
import br.edu.vet.vetbackend.entidades.TipoUsuario;
import br.edu.vet.vetbackend.entidades.Usuario;
import br.edu.vet.vetbackend.repositorios.PacienteRepository;
import br.edu.vet.vetbackend.repositorios.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Só age com o banco vazio: cria o primeiro veterinário (ADMIN_EMAIL/ADMIN_SENHA)
 * e, opcionalmente, dados de demonstração (SEED_DEMO=true). Nenhuma senha fica no código.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final UsuarioRepository usuarios;
    private final PacienteRepository pacientes;
    private final PasswordEncoder encoder;

    @Value("${app.admin.nome}") private String adminNome;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.senha}") private String adminSenha;
    @Value("${app.seed.demo}") private boolean seedDemo;
    @Value("${app.seed.senha}") private String seedSenha;

    public DadosIniciais(UsuarioRepository usuarios, PacienteRepository pacientes, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.pacientes = pacientes;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;

        if (seedDemo) {
            if (seedSenha.length() < 8) {
                log.warn("SEED_DEMO=true, mas SEED_SENHA precisa ter pelo menos 8 caracteres. Nada foi criado.");
                return;
            }
            criarDemo();
            return;
        }

        if (!adminEmail.isBlank() && adminSenha.length() >= 8) {
            usuarios.save(new Usuario(adminNome, adminEmail.trim().toLowerCase(), null,
                    encoder.encode(adminSenha), TipoUsuario.VETERINARIO));
            log.info("Primeiro veterinário criado: {}", adminEmail);
        } else {
            log.warn("Banco vazio e sem ADMIN_EMAIL/ADMIN_SENHA (mín. 8 caracteres): ninguém conseguirá entrar.");
        }
    }

    private void criarDemo() {
        String hash = encoder.encode(seedSenha);
        usuarios.save(new Usuario("Dr. Carlos Silva", "carlos@vetclinic.com", "(11) 98765-4321", hash, TipoUsuario.VETERINARIO));
        Usuario maria = usuarios.save(new Usuario("Maria Santos", "maria@email.com", "(11) 91234-5678", hash, TipoUsuario.CLIENTE));
        Usuario joao = usuarios.save(new Usuario("João Oliveira", "joao@email.com", "(11) 99876-5432", hash, TipoUsuario.CLIENTE));

        pacientes.save(new Paciente("Rex", "Cachorro", "Labrador", 5, 30.5, maria,
                "Vacinação em dia. Castrado em 2021.", "Nenhuma alergia conhecida", null));
        pacientes.save(new Paciente("Mimi", "Gato", "Persa", 3, 4.2, maria,
                "Vacinação em dia.", "Alérgica a frango", null));
        pacientes.save(new Paciente("Thor", "Cachorro", "Pastor Alemão", 7, 35.0, joao,
                "Displasia coxofemoral leve.", "Nenhuma alergia conhecida", null));
        log.info("Dados de demonstração criados (3 usuários, 3 pacientes).");
    }
}

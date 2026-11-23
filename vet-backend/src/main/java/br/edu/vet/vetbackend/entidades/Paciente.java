package br.edu.vet.vetbackend.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 50)
    private String raca;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false)
    private Double peso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dono_id", nullable = false)
    private Usuario dono;

    @Column(name = "historico_medico", length = 2000)
    private String historicoMedico;

    @Column(length = 1000)
    private String alergias;

    @Column(name = "medicamentos_atuais", length = 1000)
    private String medicamentosAtuais;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro = LocalDate.now();

    protected Paciente() {
    }

    public Paciente(String nome, String especie, String raca, Integer idade, Double peso, Usuario dono,
                    String historicoMedico, String alergias, String medicamentosAtuais) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.dono = dono;
        this.historicoMedico = historicoMedico;
        this.alergias = alergias;
        this.medicamentosAtuais = medicamentosAtuais;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public Integer getIdade() { return idade; }
    public Double getPeso() { return peso; }
    public Usuario getDono() { return dono; }
    public String getHistoricoMedico() { return historicoMedico; }
    public String getAlergias() { return alergias; }
    public String getMedicamentosAtuais() { return medicamentosAtuais; }
    public LocalDate getDataCadastro() { return dataCadastro; }
}

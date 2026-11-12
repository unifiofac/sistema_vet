import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Paciente } from '../models/paciente.model';
import { UsuarioService } from './usuario';

export type NovoPaciente = Omit<Paciente, 'id' | 'nomeDono' | 'dataCadastro'>;

// Temporário: dados do seu vet_database.sql
const PACIENTES_MOCK: Paciente[] = [
  {
    id: 1, nome: 'Rex', especie: 'Cachorro', raca: 'Labrador', idade: 5, peso: 30.5,
    donoId: 2, nomeDono: 'Maria Santos',
    historicoMedico: 'Vacinação em dia. Castrado em 2021.',
    alergias: 'Nenhuma alergia conhecida',
    dataCadastro: '2026-10-01',
  },
  {
    id: 2, nome: 'Mimi', especie: 'Gato', raca: 'Persa', idade: 3, peso: 4.2,
    donoId: 2, nomeDono: 'Maria Santos',
    historicoMedico: 'Vacinação em dia.',
    alergias: 'Alérgica a frango',
    dataCadastro: '2026-10-01',
  },
  {
    id: 3, nome: 'Thor', especie: 'Cachorro', raca: 'Pastor Alemão', idade: 7, peso: 35.0,
    donoId: 3, nomeDono: 'João Oliveira',
    historicoMedico: 'Displasia coxofemoral leve.',
    alergias: 'Nenhuma alergia conhecida',
    dataCadastro: '2026-10-01',
  },
];

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private usuarios = inject(UsuarioService);

  // equivale ao SELECT * FROM pacientes WHERE dono_id = ? ORDER BY nome
  listarPorDono(donoId: number): Observable<Paciente[]> {
    const lista = PACIENTES_MOCK
      .filter(p => p.donoId === donoId)
      .sort((a, b) => a.nome.localeCompare(b.nome));
    return of(lista);
  }

  // equivale ao SELECT do painel_veterinario.php (com JOIN do dono)
  listarTodos(): Observable<Paciente[]> {
    return of([...PACIENTES_MOCK].sort((a, b) => a.nome.localeCompare(b.nome)));
  }

  // equivale ao INSERT INTO pacientes do cadastro_paciente.php
  cadastrar(dados: NovoPaciente): Observable<Paciente> {
    const novo: Paciente = {
      ...dados,
      id: Math.max(0, ...PACIENTES_MOCK.map(p => p.id)) + 1,
      nomeDono: this.usuarios.buscarPorId(dados.donoId)?.nome,
      dataCadastro: new Date().toISOString().slice(0, 10),
    };
    PACIENTES_MOCK.push(novo);
    return of(novo);
  }
}

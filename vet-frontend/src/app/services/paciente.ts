import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Paciente } from '../models/paciente.model';

// Temporário: dados
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
  
  listarPorDono(donoId: number): Observable<Paciente[]> {
    const lista = PACIENTES_MOCK
      .filter(p => p.donoId === donoId)
      .sort((a, b) => a.nome.localeCompare(b.nome));
    return of(lista);
  }

  listarTodos(): Observable<Paciente[]> {
    return of([...PACIENTES_MOCK].sort((a, b) => a.nome.localeCompare(b.nome)));
  }
}
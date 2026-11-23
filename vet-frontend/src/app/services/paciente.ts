import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Paciente } from '../models/paciente.model';

export type NovoPaciente = Omit<Paciente, 'id' | 'nomeDono' | 'dataCadastro'>;

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/pacientes`;

  // veterinário: todos os pacientes
  listarTodos(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.base);
  }

  // cliente: só os próprios pets (o servidor decide pelo token, não por um id enviado)
  listarMeus(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.base}/meus`);
  }

  cadastrar(dados: NovoPaciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.base, dados);
  }
}

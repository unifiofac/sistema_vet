import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ProximoAgendamento {
  id: number;
  dataHora: string;
  motivo: string;
  nomePaciente: string;
  nomeVeterinario: string;
}

export interface ResumoDashboard {
  totalPacientes: number;
  totalClientes: number;
  agendamentosHoje: number;
  agendamentosPendentes: number;
  pacientesPorEspecie: Record<string, number>;
  proximosAgendamentos: ProximoAgendamento[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);

  resumo(): Observable<ResumoDashboard> {
    return this.http.get<ResumoDashboard>(`${environment.apiUrl}/api/dashboard`);
  }
}

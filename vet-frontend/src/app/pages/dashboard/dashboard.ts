import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, map, of, startWith } from 'rxjs';
import { DashboardService, ResumoDashboard } from '../../services/dashboard';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private service = inject(DashboardService);

  estado = toSignal(
    this.service.resumo().pipe(
      map(resumo => ({ resumo, erro: false })),
      catchError(() => of({ resumo: null as ResumoDashboard | null, erro: true })),
      startWith({ resumo: null as ResumoDashboard | null, erro: false }),
    ),
    { initialValue: { resumo: null as ResumoDashboard | null, erro: false } },
  );

  especies = computed(() => {
    const r = this.estado().resumo;
    if (!r) return [];
    const total = Object.values(r.pacientesPorEspecie).reduce((a, b) => a + b, 0) || 1;
    return Object.entries(r.pacientesPorEspecie).map(([nome, qtd]) => ({
      nome,
      qtd,
      percentual: Math.round((qtd / total) * 100),
    }));
  });

  carregando = computed(() => !this.estado().resumo && !this.estado().erro);
}

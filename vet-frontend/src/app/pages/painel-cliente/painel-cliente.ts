import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AuthService } from '../../services/auth';
import { PacienteService } from '../../services/paciente';

@Component({
  selector: 'app-painel-cliente',
  standalone: false,
  templateUrl: './painel-cliente.html',
  styleUrl: './painel-cliente.css',
})
export class PainelCliente {
  private pacienteService = inject(PacienteService);
  auth = inject(AuthService);

  pacientes = toSignal(
    this.pacienteService.listarMeus().pipe(catchError(() => of([]))),
    { initialValue: [] },
  );

  icone(especie: string): string {
    return especie === 'Cachorro' ? '🐕' : '🐱';
  }
}
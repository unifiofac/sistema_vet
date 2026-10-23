import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { AuthService } from '../../services/auth';
import { PacienteService } from '../../services/paciente';

@Component({
  selector: 'app-painel-cliente',
  imports: [DatePipe],
  templateUrl: './painel-cliente.html',
  styleUrl: './painel-cliente.css',
})
export class PainelCliente {
  private pacienteService = inject(PacienteService);
  auth = inject(AuthService);

  pacientes = toSignal(
    this.pacienteService.listarPorDono(this.auth.usuario()!.id),
    { initialValue: [] },
  );

  icone(especie: string): string {
    return especie === 'Cachorro' ? '🐕' : '🐱';
  }
}
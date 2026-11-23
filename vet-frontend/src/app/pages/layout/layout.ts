import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-layout',
  standalone: false,
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {
  auth = inject(AuthService);

  itens = [
    { rota: 'dashboard', rotulo: 'Dashboard', icone: 'dashboard' },
    { rota: 'agendamentos', rotulo: 'Agendamentos', icone: 'event' },
    { rota: 'pacientes', rotulo: 'Pacientes', icone: 'pets' },
    { rota: 'clientes', rotulo: 'Clientes', icone: 'people' },
    { rota: 'veterinarios', rotulo: 'Veterinários', icone: 'medical_services' },
    { rota: 'recepcionistas', rotulo: 'Recepcionistas', icone: 'support_agent' },
  ];
}

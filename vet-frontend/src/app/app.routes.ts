import { Routes } from '@angular/router';
import { clienteGuard, veterinarioGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./pages/login/login').then(m => m.Login) },
  {
    path: 'veterinario',
    canActivate: [veterinarioGuard],
    loadComponent: () => import('./pages/painel-veterinario/painel-veterinario').then(m => m.PainelVeterinario),
  },
  {
    path: 'veterinario/pacientes/novo',
    canActivate: [veterinarioGuard],
    loadComponent: () => import('./pages/cadastro-paciente/cadastro-paciente').then(m => m.CadastroPaciente),
  },
  {
    path: 'cliente',
    canActivate: [clienteGuard],
    loadComponent: () => import('./pages/painel-cliente/painel-cliente').then(m => m.PainelCliente),
  },
  { path: '**', redirectTo: 'login' },
];
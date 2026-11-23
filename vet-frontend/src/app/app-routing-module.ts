import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { clienteGuard, veterinarioGuard } from './guards/auth.guard';
import { CrudPagina } from './pages/crud/crud-pagina';
import { Dashboard } from './pages/dashboard/dashboard';
import { Layout } from './pages/layout/layout';
import { Login } from './pages/login/login';
import { PainelCliente } from './pages/painel-cliente/painel-cliente';
import { PainelVeterinario } from './pages/painel-veterinario/painel-veterinario';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'app',
    component: Layout,
    canActivate: [veterinarioGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'pacientes', component: CrudPagina, data: { entidade: 'pacientes' } },
      { path: 'clientes', component: CrudPagina, data: { entidade: 'clientes' } },
      { path: 'agendamentos', component: CrudPagina, data: { entidade: 'agendamentos' } },
      { path: 'veterinarios', component: CrudPagina, data: { entidade: 'veterinarios' } },
      { path: 'recepcionistas', component: CrudPagina, data: { entidade: 'recepcionistas' } },
      { path: 'ia', component: PainelVeterinario },
    ],
  },
  { path: 'cliente', component: PainelCliente, canActivate: [clienteGuard] },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

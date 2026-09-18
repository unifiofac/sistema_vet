import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Cadastro } from './auth/cadastro/cadastro';
import { CadastroPet } from './pets/cadastro-pet/cadastro-pet';
import { PainelPet } from './pets/painel-pet/painel-pet';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'cadastro', component: Cadastro },
  { path: 'pets/cadastro', component: CadastroPet },
  { path: 'pets/painel', component: PainelPet },
  { path: 'pets', redirectTo: 'pets/painel', pathMatch: 'full' },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
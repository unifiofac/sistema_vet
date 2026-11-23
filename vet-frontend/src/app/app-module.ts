import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { NgModule, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { App } from './app';
import { AppRoutingModule } from './app-routing-module';
import { CrudDialogo } from './pages/crud/crud-dialogo';
import { CrudPagina } from './pages/crud/crud-pagina';
import { ConfirmarDialogo } from './pages/crud/confirmar-dialogo';
import { Dashboard } from './pages/dashboard/dashboard';
import { Layout } from './pages/layout/layout';
import { Login } from './pages/login/login';
import { PainelCliente } from './pages/painel-cliente/painel-cliente';
import { PainelVeterinario } from './pages/painel-veterinario/painel-veterinario';
import { authInterceptor } from './services/auth.interceptor';
import { MaterialModule } from './shared/material.module';

@NgModule({
  declarations: [
    App,
    Login,
    Layout,
    Dashboard,
    CrudPagina,
    CrudDialogo,
    ConfirmarDialogo,
    PainelCliente,
    PainelVeterinario,
  ],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, AppRoutingModule, MaterialModule],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
  bootstrap: [App],
})
export class AppModule {}

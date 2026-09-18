import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../auth';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = '';
  senha = '';
  lembrar = false;
  erro = '';

  constructor(private auth: Auth, private router: Router) {}

  entrar() {
    this.erro = '';

    if (!this.email || !this.senha) {
      this.erro = 'Por favor, preencha todos os campos!';
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      this.erro = 'Por favor, insira um e-mail válido!';
      return;
    }

    const usuario = this.auth.login(this.email, this.senha);
    if (!usuario) {
      this.erro = 'E-mail ou senha incorretos!';
      return;
    }

    if (usuario.tipo === 'veterinario') {
      this.router.navigate(['/veterinario']);
    } else {
      this.router.navigate(['/cliente']);
    }
  }
}
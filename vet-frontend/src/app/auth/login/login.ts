import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  email = '';
  senha = '';
  mensagem = '';
  tipoMensagem: 'erro' | 'sucesso' | '' = '';

  constructor(private router: Router) {}

  validarEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  exibirMensagem(msg: string, tipo: 'erro' | 'sucesso') {
    this.mensagem = msg;
    this.tipoMensagem = tipo;
    setTimeout(() => (this.mensagem = ''), 5000);
  }

  realizarLogin() {
    if (!this.email || !this.senha) {
      this.exibirMensagem('Por favor, preencha todos os campos!', 'erro');
      return;
    }
    if (!this.validarEmail(this.email)) {
      this.exibirMensagem('E-mail inválido!', 'erro');
      return;
    }

    // TODO: substituir pela chamada real ao backend
    this.exibirMensagem('Login realizado com sucesso! Redirecionando...', 'sucesso');
    setTimeout(() => this.router.navigate(['/pets']), 2000);
  }

  esqueceuSenha() {
    alert('Funcionalidade de recuperação de senha será implementada em breve!');
  }
}
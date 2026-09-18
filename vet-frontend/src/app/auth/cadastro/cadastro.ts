import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css'
})
export class Cadastro {
  email = '';
  ddd = '';
  celular = '';
  senha = '';
  confirmarSenha = '';

  mensagem = '';
  tipoMensagem: 'erro' | 'sucesso' | '' = '';

  constructor(private router: Router) {}

  private validarEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  private validarDDD(ddd: string): boolean {
    return ddd.length === 2 && !isNaN(Number(ddd));
  }

  private validarCelular(celular: string): boolean {
    return celular.length === 9 && !isNaN(Number(celular));
  }

  private exibirMensagem(msg: string, tipo: 'erro' | 'sucesso') {
    this.mensagem = msg;
    this.tipoMensagem = tipo;
    setTimeout(() => (this.mensagem = ''), 5000);
  }

  realizarCadastro() {
    if (!this.email || !this.ddd || !this.celular || !this.senha || !this.confirmarSenha) {
      this.exibirMensagem('Por favor, preencha todos os campos!', 'erro');
      return;
    }

    if (!this.validarEmail(this.email)) {
      this.exibirMensagem('E-mail inválido!', 'erro');
      return;
    }

    if (!this.validarDDD(this.ddd)) {
      this.exibirMensagem('DDD inválido! Digite apenas 2 números.', 'erro');
      return;
    }

    if (!this.validarCelular(this.celular)) {
      this.exibirMensagem('Número de celular inválido! Digite 9 dígitos.', 'erro');
      return;
    }

    if (this.senha.length < 6) {
      this.exibirMensagem('A senha deve ter no mínimo 6 caracteres!', 'erro');
      return;
    }

    if (this.senha !== this.confirmarSenha) {
      this.exibirMensagem('As senhas não coincidem!', 'erro');
      return;
    }

    // TODO: substituir pela chamada real ao backend (AuthService.cadastrar)
    this.exibirMensagem('Cadastro realizado com sucesso! Faça login para continuar.', 'sucesso');

    this.email = '';
    this.ddd = '';
    this.celular = '';
    this.senha = '';
    this.confirmarSenha = '';

    setTimeout(() => this.router.navigate(['/login']), 1500);
  }
}
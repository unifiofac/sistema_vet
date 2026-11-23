import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  erro = signal('');
  carregando = signal(false);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', Validators.required],
  });

  entrar() {
    this.erro.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, senha } = this.form.getRawValue();
    this.carregando.set(true);

    this.auth.login(email.trim(), senha).subscribe({
      next: usuario => {
        this.carregando.set(false);
        this.router.navigate([usuario.tipo === 'cliente' ? '/cliente' : '/app']);
      },
      error: (e: HttpErrorResponse) => {
        this.carregando.set(false);
        this.erro.set(
          e.status === 401 ? 'E-mail ou senha incorretos.'
          : e.status === 429 ? 'Muitas tentativas. Aguarde um minuto e tente de novo.'
          : 'Não foi possível conectar ao servidor. Tente novamente em instantes.',
        );
      },
    });
  }
}

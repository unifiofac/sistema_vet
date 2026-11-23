import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario } from '../models/usuario.model';

interface RespostaLogin {
  token: string;
  usuario: Usuario;
}

interface Sessao {
  token: string;
  usuario: Usuario;
}

const CHAVE = 'vet_sessao';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private _sessao = signal<Sessao | null>(this.carregar());
  readonly usuario = computed(() => this._sessao()?.usuario ?? null);
  readonly token = computed(() => this._sessao()?.token ?? null);
  readonly logado = computed(() => this._sessao() !== null);

  login(email: string, senha: string): Observable<Usuario> {
    return this.http
      .post<RespostaLogin>(`${environment.apiUrl}/api/auth/login`, { email, senha })
      .pipe(
        tap(r => {
          const sessao = { token: r.token, usuario: r.usuario };
          this._sessao.set(sessao);
          try { sessionStorage.setItem(CHAVE, JSON.stringify(sessao)); } catch {}
        }),
        map(r => r.usuario),
      );
  }

  logout(): void {
    this._sessao.set(null);
    try { sessionStorage.removeItem(CHAVE); } catch {}
    this.router.navigate(['/login']);
  }

  private carregar(): Sessao | null {
    try {
      const salvo = sessionStorage.getItem(CHAVE);
      if (!salvo) return null;
      const sessao = JSON.parse(salvo) as Sessao;
      return this.expirado(sessao.token) ? null : sessao;
    } catch {
      return null;
    }
  }

  private expirado(token: string): boolean {
    try {
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const { exp } = JSON.parse(atob(payload));
      return typeof exp !== 'number' || exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}

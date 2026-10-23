import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Usuario } from '../models/usuario.model';

interface UsuarioMock extends Usuario {
  senha: string;
}

// Temporário: dados do seu vet_database.sql. Quando o Spring Boot existir,
// isso vira uma chamada HTTP e a senha nunca mais fica no frontend.
const USUARIOS_MOCK: UsuarioMock[] = [
  { id: 1, nome: 'Dr. Carlos Silva', email: 'carlos@vetclinic.com', telefone: '(11) 98765-4321', tipo: 'veterinario', senha: 'senha123' },
  { id: 2, nome: 'Maria Santos', email: 'maria@email.com', telefone: '(11) 91234-5678', tipo: 'cliente', senha: 'senha123' },
  { id: 3, nome: 'João Oliveira', email: 'joao@email.com', telefone: '(11) 99876-5432', tipo: 'cliente', senha: 'senha123' },
];

const CHAVE = 'vet_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);

  // equivale ao $_SESSION do PHP
  private _usuario = signal<Usuario | null>(this.carregar());
  readonly usuario = this._usuario.asReadonly();
  readonly logado = computed(() => this._usuario() !== null);

  login(email: string, senha: string): Usuario | null {
    const encontrado = USUARIOS_MOCK.find(u => u.email === email && u.senha === senha);
    if (!encontrado) return null;

    const { senha: _ignorada, ...usuario } = encontrado;
    this._usuario.set(usuario);
    try { sessionStorage.setItem(CHAVE, JSON.stringify(usuario)); } catch {}
    return usuario;
  }

  logout(): void {
    this._usuario.set(null);
    try { sessionStorage.removeItem(CHAVE); } catch {}
    this.router.navigate(['/login']);
  }

  private carregar(): Usuario | null {
    try {
      const salvo = sessionStorage.getItem(CHAVE);
      return salvo ? JSON.parse(salvo) : null;
    } catch {
      return null;
    }
  }
}
import { Injectable, signal } from '@angular/core';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  tipo: 'cliente' | 'veterinario';
}

// Mock temporário — os mesmos usuários do vet_database.sql (senha: senha123 pra todos)
const USUARIOS_MOCK: (Usuario & { senha: string })[] = [
  { id: 1, nome: 'Dr. Carlos Silva', email: 'carlos@vetclinic.com', senha: 'senha123', tipo: 'veterinario' },
  { id: 2, nome: 'Maria Santos', email: 'maria@email.com', senha: 'senha123', tipo: 'cliente' },
  { id: 3, nome: 'João Oliveira', email: 'joao@email.com', senha: 'senha123', tipo: 'cliente' }
];

@Injectable({ providedIn: 'root' })
export class Auth {
  usuarioAtual = signal<Usuario | null>(null);

  login(email: string, senha: string): Usuario | null {
    const encontrado = USUARIOS_MOCK.find(u => u.email === email && u.senha === senha);
    if (!encontrado) return null;

    const { senha: _senha, ...usuario } = encontrado;
    this.usuarioAtual.set(usuario);
    return usuario;
  }

  logout() {
    this.usuarioAtual.set(null);
  }
}
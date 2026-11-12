import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Usuario } from '../models/usuario.model';

// Temporário: clientes do seu vet_database.sql
const CLIENTES_MOCK: Usuario[] = [
  { id: 2, nome: 'Maria Santos', email: 'maria@email.com', telefone: '(11) 91234-5678', tipo: 'cliente' },
  { id: 3, nome: 'João Oliveira', email: 'joao@email.com', telefone: '(11) 99876-5432', tipo: 'cliente' },
];

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  // equivale ao SELECT id, nome, email FROM usuarios WHERE tipo = 'cliente' ORDER BY nome
  listarClientes(): Observable<Usuario[]> {
    return of([...CLIENTES_MOCK].sort((a, b) => a.nome.localeCompare(b.nome)));
  }

  buscarPorId(id: number): Usuario | undefined {
    return CLIENTES_MOCK.find(c => c.id === id);
  }
}

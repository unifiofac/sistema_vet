import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);

  listarClientes(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${environment.apiUrl}/api/clientes`);
  }
}

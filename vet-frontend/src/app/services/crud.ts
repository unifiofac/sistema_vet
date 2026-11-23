import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type Registro = Record<string, any>;

@Injectable({ providedIn: 'root' })
export class CrudService {
  private http = inject(HttpClient);

  private url(endpoint: string, id?: number): string {
    return `${environment.apiUrl}${endpoint}${id === undefined ? '' : '/' + id}`;
  }

  listar(endpoint: string): Observable<Registro[]> {
    return this.http.get<Registro[]>(this.url(endpoint));
  }

  criar(endpoint: string, dados: Registro): Observable<Registro> {
    return this.http.post<Registro>(this.url(endpoint), dados);
  }

  atualizar(endpoint: string, id: number, dados: Registro): Observable<Registro> {
    return this.http.put<Registro>(this.url(endpoint, id), dados);
  }

  excluir(endpoint: string, id: number): Observable<void> {
    return this.http.delete<void>(this.url(endpoint, id));
  }
}

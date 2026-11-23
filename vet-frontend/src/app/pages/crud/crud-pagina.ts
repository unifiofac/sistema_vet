import { formatDate } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth';
import { CrudService, Registro } from '../../services/crud';
import { Coluna, ENTIDADES, Entidade } from '../../shared/entidades';
import { ConfirmarDialogo } from './confirmar-dialogo';
import { CrudDialogo } from './crud-dialogo';

const ROTULOS_STATUS: Record<string, string> = {
  AGENDADO: 'Agendado',
  CONCLUIDO: 'Concluído',
  CANCELADO: 'Cancelado',
};

@Component({
  selector: 'app-crud-pagina',
  standalone: false,
  templateUrl: './crud-pagina.html',
  styleUrl: './crud-pagina.css',
})
export class CrudPagina {
  private crud = inject(CrudService);
  private dialog = inject(MatDialog);
  private aviso = inject(MatSnackBar);
  private auth = inject(AuthService);
  private rota = inject(ActivatedRoute);

  private dados = toSignal(this.rota.data, { initialValue: this.rota.snapshot.data });
  entidade = computed<Entidade>(() => ENTIDADES[this.dados()['entidade']]);

  registros = signal<Registro[]>([]);
  carregando = signal(true);
  erro = signal('');

  colunas = computed(() => [...this.entidade().colunas.map(c => c.chave), 'acoes']);
  ehVeterinario = computed(() => this.auth.usuario()?.tipo === 'veterinario');
  podeEscrever = computed(() => !this.entidade().somenteVeterinarioEscreve || this.ehVeterinario());

  constructor() {
    this.recarregar();
  }

  recarregar() {
    this.carregando.set(true);
    this.erro.set('');
    this.crud.listar(this.entidade().endpoint).subscribe({
      next: lista => {
        this.registros.set(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar a lista.');
      },
    });
  }

  valor(registro: Registro, coluna: Coluna): string {
    const v = registro[coluna.chave];
    if (v === null || v === undefined || v === '') return '—';
    switch (coluna.formato) {
      case 'dataHora': return formatDate(v, 'dd/MM/yyyy HH:mm', 'en-US');
      case 'status': return ROTULOS_STATUS[v] ?? v;
      case 'kg': return `${v} kg`;
      case 'anos': return `${v} ${v === 1 ? 'ano' : 'anos'}`;
      default: return String(v);
    }
  }

  abrir(registro?: Registro) {
    this.dialog
      .open(CrudDialogo, { data: { entidade: this.entidade(), registro }, maxHeight: '90vh' })
      .afterClosed()
      .subscribe(salvou => {
        if (salvou) {
          this.aviso.open('Salvo com sucesso.', 'OK', { duration: 3000 });
          this.recarregar();
        }
      });
  }

  excluir(registro: Registro) {
    const nome = registro['nome'] ?? registro['nomePaciente'] ?? `#${registro['id']}`;
    this.dialog
      .open(ConfirmarDialogo, { data: { mensagem: `Excluir ${this.entidade().singular} "${nome}"?` } })
      .afterClosed()
      .subscribe(ok => {
        if (!ok) return;
        this.crud.excluir(this.entidade().endpoint, registro['id']).subscribe({
          next: () => {
            this.aviso.open('Excluído.', 'OK', { duration: 3000 });
            this.recarregar();
          },
          error: (e: HttpErrorResponse) =>
            this.aviso.open(e.error?.mensagem ?? 'Não foi possível excluir.', 'OK', { duration: 6000 }),
        });
      });
  }
}

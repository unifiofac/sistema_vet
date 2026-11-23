import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { forkJoin, of } from 'rxjs';
import { Campo, Entidade } from '../../shared/entidades';
import { CrudService, Registro } from '../../services/crud';

export interface DadosDialogo {
  entidade: Entidade;
  registro?: Registro;
}

@Component({
  selector: 'app-crud-dialogo',
  standalone: false,
  templateUrl: './crud-dialogo.html',
  styleUrl: './crud-dialogo.css',
})
export class CrudDialogo {
  private crud = inject(CrudService);
  private ref = inject(MatDialogRef<CrudDialogo, boolean>);
  dados = inject<DadosDialogo>(MAT_DIALOG_DATA);

  entidade = this.dados.entidade;
  edicao = !!this.dados.registro;
  erro = signal('');
  salvando = signal(false);
  opcoes = signal<Record<string, { valor: any; rotulo: string }[]>>({});

  form = new FormGroup(Object.fromEntries(this.entidade.campos.map(c => [c.chave, this.criarControle(c)])));

  constructor() {
    const pedidos: Record<string, any> = {};
    for (const campo of this.entidade.campos) {
      if (campo.opcoesDe) pedidos[campo.chave] = this.crud.listar(campo.opcoesDe.endpoint);
    }
    if (Object.keys(pedidos).length) {
      forkJoin(pedidos).subscribe({
        next: (r: any) => {
          const mapa: Record<string, { valor: any; rotulo: string }[]> = {};
          for (const campo of this.entidade.campos) {
            if (campo.opcoesDe) {
              mapa[campo.chave] = (r[campo.chave] as Registro[]).map(x => ({
                valor: x['id'],
                rotulo: x[campo.opcoesDe!.rotulo],
              }));
            }
          }
          this.opcoes.set(mapa);
        },
        error: () => this.erro.set('Não foi possível carregar as listas de opções.'),
      });
    }
  }

  private criarControle(campo: Campo): FormControl {
    const validadores = [];
    const senhaObrigatoriaNoCadastro = campo.tipo === 'password' && !this.dados.registro;
    if (campo.obrigatorio || senhaObrigatoriaNoCadastro) validadores.push(Validators.required);
    if (campo.tipo === 'email') validadores.push(Validators.email);
    if (campo.tipo === 'number') validadores.push(Validators.min(0));
    if (campo.max && campo.tipo !== 'number') validadores.push(Validators.maxLength(campo.max));
    if (campo.tipo === 'password') validadores.push(Validators.minLength(6));
    return new FormControl(this.valorInicial(campo), validadores);
  }

  private valorInicial(campo: Campo): any {
    const reg = this.dados.registro;
    if (!reg || campo.tipo === 'password') return campo.tipo === 'select' ? null : '';
    const v = reg[campo.chave];
    if (v === null || v === undefined) return campo.tipo === 'select' || campo.tipo === 'number' ? null : '';
    return campo.tipo === 'datetime' ? String(v).slice(0, 16) : v;
  }

  mensagemErro(campo: Campo): string {
    const erros: ValidationErrors | null = this.form.get(campo.chave)?.errors ?? null;
    if (!erros) return '';
    if (erros['required']) return 'Campo obrigatório';
    if (erros['email']) return 'E-mail inválido';
    if (erros['min']) return 'Não pode ser negativo';
    if (erros['minlength']) return 'Mínimo de 6 caracteres';
    if (erros['maxlength']) return `Máximo de ${campo.max} caracteres`;
    return 'Valor inválido';
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.erro.set('');
    this.salvando.set(true);

    const corpo: Registro = {};
    for (const campo of this.entidade.campos) {
      const v = this.form.get(campo.chave)!.value;
      if (v === '' || v === null) {
        if (campo.tipo !== 'password') corpo[campo.chave] = null;
      } else {
        corpo[campo.chave] = typeof v === 'string' ? v.trim() : v;
      }
    }

    const id = this.dados.registro?.['id'];
    const chamada = this.edicao
      ? this.crud.atualizar(this.entidade.endpoint, id, corpo)
      : this.crud.criar(this.entidade.endpoint, corpo);

    chamada.subscribe({
      next: () => this.ref.close(true),
      error: (e: HttpErrorResponse) => {
        this.salvando.set(false);
        const campos = e.error?.campos ? Object.values(e.error.campos).join(' ') : '';
        this.erro.set(
          e.error?.mensagem && e.status !== 400
            ? e.error.mensagem
            : campos || e.error?.mensagem || 'Não foi possível salvar. Tente novamente.',
        );
      },
    });
  }

  cancelar() {
    this.ref.close(false);
  }
}

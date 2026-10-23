import { Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Paciente } from '../../models/paciente.model';
import { AuthService } from '../../services/auth';
import { escapar, IaService, MensagemHistorico } from '../../services/ia';
import { PacienteService } from '../../services/paciente';

interface Mensagem {
  tipo: 'ai' | 'user';
  html: string;
  texto: string;      // versão em texto puro, usada no histórico enviado à IA
  puro?: boolean;     // resposta da IA: respeita quebras de linha
  erro?: boolean;     // mensagem de falha: não entra no histórico
}

@Component({
  selector: 'app-painel-veterinario',
  imports: [FormsModule, RouterLink],
  templateUrl: './painel-veterinario.html',
  styleUrl: './painel-veterinario.css',
})
export class PainelVeterinario {
  auth = inject(AuthService);
  private pacienteService = inject(PacienteService);
  private ia = inject(IaService);

  pacientes = toSignal(this.pacienteService.listarTodos(), { initialValue: [] });

  selecionado = signal<Paciente | null>(null);
  mensagens = signal<Mensagem[]>([]);
  digitando = signal(false);
  texto = signal('');

  private chat = viewChild<ElementRef<HTMLElement>>('chat');

  icone(especie: string): string {
    return especie === 'Cachorro' ? '🐕' : '🐱';
  }

  selecionar(paciente: Paciente) {
    this.selecionado.set(paciente);
    this.digitando.set(false);
    this.mensagens.set([{ tipo: 'ai', html: this.ia.resumoPaciente(paciente), texto: '' }]);
    this.rolar();
  }

  enviar() {
    const pet = this.selecionado();
    const msg = this.texto().trim();
    if (!pet || !msg || this.digitando()) return;

    // histórico = conversa real até aqui (sem o resumo inicial nem mensagens de erro)
    const historico = this.mensagens()
      .slice(1)
      .filter(m => !m.erro)
      .map((m): MensagemHistorico => ({ papel: m.tipo === 'user' ? 'user' : 'model', texto: m.texto }))
      .slice(-10);
    while (historico.length && historico[0].papel === 'model') historico.shift();

    this.mensagens.update(l => [...l, { tipo: 'user', html: escapar(msg), texto: msg }]);
    this.texto.set('');
    this.digitando.set(true);
    this.rolar();

    this.ia.responder(msg, pet, historico).subscribe({
      next: resposta =>
        this.receber(pet, { tipo: 'ai', html: escapar(resposta), texto: resposta, puro: true }),
      error: () =>
        this.receber(pet, {
          tipo: 'ai',
          html: 'Não consegui consultar a IA agora. Tente novamente em instantes.',
          texto: '',
          erro: true,
        }),
    });
  }

  teclaEnter(evento: Event) {
    const e = evento as KeyboardEvent;
    if (!e.shiftKey) {
      e.preventDefault();
      this.enviar();
    }
  }

  private receber(pet: Paciente, mensagem: Mensagem) {
    // se trocou de paciente durante a espera, descarta a resposta antiga
    if (this.selecionado() !== pet) return;
    this.mensagens.update(l => [...l, mensagem]);
    this.digitando.set(false);
    this.rolar();
  }

  private rolar() {
    setTimeout(() => {
      const el = this.chat()?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    });
  }
}
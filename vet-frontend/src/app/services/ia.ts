import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Paciente } from '../models/paciente.model';

// Escapa texto vindo de usuário/banco/IA antes de entrar em HTML
export function escapar(texto: string | undefined | null): string {
  return (texto ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

export interface MensagemHistorico {
  papel: 'user' | 'model';
  texto: string;
}

@Injectable({ providedIn: 'root' })
export class IaService {
  private http = inject(HttpClient);

  responder(pergunta: string, pet: Paciente, historico: MensagemHistorico[]): Observable<string> {
    const body = {
      pergunta,
      // só o necessário para a IA: dados do dono não são enviados
      paciente: {
        nome: pet.nome,
        especie: pet.especie,
        raca: pet.raca,
        idade: pet.idade,
        peso: pet.peso,
        historicoMedico: pet.historicoMedico,
        alergias: pet.alergias,
        medicamentosAtuais: pet.medicamentosAtuais,
      },
      historico,
    };
    return this.http.post<{ resposta: string }>('/api/ia', body).pipe(map(r => r.resposta));
  }

  // Mensagem inicial ao selecionar um paciente
  resumoPaciente(pet: Paciente): string {
    return `
      <p><strong>Paciente selecionado: ${escapar(pet.nome)}</strong></p>
      <p><strong>Informações:</strong></p>
      <ul>
        <li><strong>Espécie:</strong> ${escapar(pet.especie)}</li>
        <li><strong>Raça:</strong> ${escapar(pet.raca) || 'Não especificada'}</li>
        <li><strong>Idade:</strong> ${pet.idade} anos</li>
        <li><strong>Peso:</strong> ${pet.peso} kg</li>
        <li><strong>Dono:</strong> ${escapar(pet.nomeDono)}</li>
        ${pet.alergias ? `<li><strong>⚠️ Alergias:</strong> ${escapar(pet.alergias)}</li>` : ''}
        ${pet.historicoMedico ? `<li><strong>Histórico:</strong> ${escapar(pet.historicoMedico)}</li>` : ''}
      </ul>
      <p>Como posso ajudar com este paciente?</p>`;
  }
}
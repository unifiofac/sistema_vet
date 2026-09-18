import { Injectable, signal, computed } from '@angular/core';

export interface Pet {
  id: number;
  nome: string;
  especie: string;
  idade: string;
  sexo: string;
  foto: string | null;
  tutorNome: string;
  tutorContato: string;
  historicoMedico: string;
  status: string;
  dataCadastro: string;
  historico: { tipo: string; data: string }[];
}

@Injectable({ providedIn: 'root' })
export class Pets {
  private petsSignal = signal<Pet[]>([]);
  private selectedIdSignal = signal<number | null>(null);

  pets = this.petsSignal.asReadonly();

  selectedPet = computed(() =>
    this.petsSignal().find(p => p.id === this.selectedIdSignal()) ?? null
  );

  cadastrarPet(dados: Omit<Pet, 'id' | 'status' | 'dataCadastro' | 'historico'>) {
    const novoPet: Pet = {
      ...dados,
      id: Date.now(),
      status: 'Cadastrado',
      dataCadastro: new Date().toLocaleDateString('pt-BR'),
      historico: [{ tipo: 'Cadastro', data: new Date().toLocaleDateString('pt-BR') }]
    };
    this.petsSignal.update(list => [...list, novoPet]);
    this.selectedIdSignal.set(novoPet.id);
  }

  selecionarPet(id: number) {
    this.selectedIdSignal.set(id);
  }

  adicionarConsulta() {
    this.atualizarPetSelecionado(pet => {
      pet.status = 'Em consulta';
      pet.historico.push({ tipo: 'Consulta de rotina', data: new Date().toLocaleDateString('pt-BR') });
    });
  }

  adicionarVacinacao() {
    this.atualizarPetSelecionado(pet => {
      pet.historico.push({ tipo: 'Vacinação', data: new Date().toLocaleDateString('pt-BR') });
    });
  }

  private atualizarPetSelecionado(fn: (pet: Pet) => void) {
    const id = this.selectedIdSignal();
    this.petsSignal.update(list =>
      list.map(p => {
        if (p.id !== id) return p;
        const copia = { ...p, historico: [...p.historico] };
        fn(copia);
        return copia;
      })
    );
  }
}
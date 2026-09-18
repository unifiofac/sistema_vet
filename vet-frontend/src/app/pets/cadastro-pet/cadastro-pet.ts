import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Pets } from '../pets';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cadastro-pet',
  imports: [FormsModule],
  templateUrl: './cadastro-pet.html',
  styleUrl: './cadastro-pet.css'
})
export class CadastroPet {
  nome = '';
  especie = '';
  idade = '';
  sexo = '';
  tutorNome = '';
  tutorContato = '';
  historicoMedico = '';
  imagePreview: string | null = null;

  constructor(private petsService: Pets, private router: Router) {}

  onFotoSelecionada(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onloadend = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  cadastrar() {
    if (!this.nome || !this.especie || !this.idade || !this.sexo || !this.tutorNome || !this.tutorContato) {
      alert('Por favor, preencha todos os campos obrigatórios');
      return;
    }

    this.petsService.cadastrarPet({
      nome: this.nome,
      especie: this.especie,
      idade: this.idade,
      sexo: this.sexo,
      foto: this.imagePreview,
      tutorNome: this.tutorNome,
      tutorContato: this.tutorContato,
      historicoMedico: this.historicoMedico
    });

    this.router.navigate(['/pets/painel']);
  }
}
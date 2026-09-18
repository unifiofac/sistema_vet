import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Pets } from '../pets';

@Component({
  selector: 'app-painel-pet',
  imports: [CommonModule, RouterLink],
  templateUrl: './painel-pet.html',
  styleUrl: './painel-pet.css'
})
export class PainelPet {
  constructor(public petsService: Pets) {}

  get pets() {
    return this.petsService.pets();
  }

  get petSelecionado() {
    return this.petsService.selectedPet();
  }

  selecionar(id: number) {
    this.petsService.selecionarPet(id);
  }

  adicionarConsulta() {
    this.petsService.adicionarConsulta();
  }

  adicionarVacinacao() {
    this.petsService.adicionarVacinacao();
  }
}
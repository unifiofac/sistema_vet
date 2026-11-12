import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { PacienteService } from '../../services/paciente';
import { UsuarioService } from '../../services/usuario';

@Component({
  selector: 'app-cadastro-paciente',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro-paciente.html',
  styleUrl: './cadastro-paciente.css',
})
export class CadastroPaciente {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private pacienteService = inject(PacienteService);
  private usuarioService = inject(UsuarioService);
  auth = inject(AuthService);

  clientes = toSignal(this.usuarioService.listarClientes(), { initialValue: [] });
  especies = ['Cachorro', 'Gato', 'Pássaro', 'Coelho', 'Outro'];

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    especie: ['', Validators.required],
    raca: ['', Validators.maxLength(50)],
    idade: [null as number | null, [Validators.required, Validators.min(0)]],
    peso: [null as number | null, [Validators.required, Validators.min(0)]],
    donoId: [null as number | null, Validators.required],
    historicoMedico: [''],
    alergias: [''],
    medicamentosAtuais: [''],
  });

  invalido(campo: AbstractControl): boolean {
    return campo.touched && campo.invalid;
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!confirm('Confirma o cadastro deste paciente?')) return;

    const v = this.form.getRawValue();
    this.pacienteService
      .cadastrar({
        nome: v.nome.trim(),
        especie: v.especie,
        raca: v.raca.trim() || undefined,
        idade: v.idade!,
        peso: v.peso!,
        donoId: v.donoId!,
        historicoMedico: v.historicoMedico.trim() || undefined,
        alergias: v.alergias.trim() || undefined,
        medicamentosAtuais: v.medicamentosAtuais.trim() || undefined,
      })
      .subscribe(() => this.router.navigate(['/veterinario']));
  }
}

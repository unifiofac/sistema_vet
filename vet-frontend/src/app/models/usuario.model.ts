export type TipoUsuario = 'cliente' | 'veterinario';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  tipo: TipoUsuario;
}
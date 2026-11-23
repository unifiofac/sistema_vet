export type TipoUsuario = 'cliente' | 'veterinario' | 'recepcionista';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  tipo: TipoUsuario;
}
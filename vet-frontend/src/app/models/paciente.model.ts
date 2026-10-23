export interface Paciente {
  id: number;
  nome: string;
  especie: string;
  raca?: string;
  idade: number;
  peso: number;
  donoId: number;
  nomeDono?: string;
  historicoMedico?: string;
  alergias?: string;
  medicamentosAtuais?: string;
  dataCadastro?: string;
}
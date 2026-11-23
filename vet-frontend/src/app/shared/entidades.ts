export type TipoCampo = 'text' | 'email' | 'number' | 'password' | 'select' | 'datetime' | 'textarea';

export interface Campo {
  chave: string;
  rotulo: string;
  tipo: TipoCampo;
  obrigatorio?: boolean;
  max?: number;
  opcoes?: { valor: string; rotulo: string }[];
  opcoesDe?: { endpoint: string; rotulo: string };
  passo?: string;
  largura?: 'inteira';
}

export interface Coluna {
  chave: string;
  titulo: string;
  formato?: 'dataHora' | 'status' | 'kg' | 'anos';
}

export interface Entidade {
  chave: string;
  titulo: string;
  singular: string;
  endpoint: string;
  somenteVeterinarioEscreve?: boolean;
  colunas: Coluna[];
  campos: Campo[];
}

const ESPECIES = ['Cachorro', 'Gato', 'Pássaro', 'Coelho', 'Outro'].map(e => ({ valor: e, rotulo: e }));
const STATUS = [
  { valor: 'AGENDADO', rotulo: 'Agendado' },
  { valor: 'CONCLUIDO', rotulo: 'Concluído' },
  { valor: 'CANCELADO', rotulo: 'Cancelado' },
];

export const ENTIDADES: Record<string, Entidade> = {
  pacientes: {
    chave: 'pacientes',
    titulo: 'Pacientes',
    singular: 'paciente',
    endpoint: '/api/pacientes',
    colunas: [
      { chave: 'nome', titulo: 'Nome' },
      { chave: 'especie', titulo: 'Espécie' },
      { chave: 'raca', titulo: 'Raça' },
      { chave: 'idade', titulo: 'Idade', formato: 'anos' },
      { chave: 'peso', titulo: 'Peso', formato: 'kg' },
      { chave: 'nomeDono', titulo: 'Dono' },
    ],
    campos: [
      { chave: 'nome', rotulo: 'Nome', tipo: 'text', obrigatorio: true, max: 100 },
      { chave: 'especie', rotulo: 'Espécie', tipo: 'select', obrigatorio: true, opcoes: ESPECIES },
      { chave: 'raca', rotulo: 'Raça', tipo: 'text', max: 50 },
      { chave: 'idade', rotulo: 'Idade (anos)', tipo: 'number', obrigatorio: true },
      { chave: 'peso', rotulo: 'Peso (kg)', tipo: 'number', obrigatorio: true, passo: '0.1' },
      {
        chave: 'donoId',
        rotulo: 'Dono',
        tipo: 'select',
        obrigatorio: true,
        opcoesDe: { endpoint: '/api/clientes', rotulo: 'nome' },
      },
      { chave: 'historicoMedico', rotulo: 'Histórico médico', tipo: 'textarea', max: 2000, largura: 'inteira' },
      { chave: 'alergias', rotulo: 'Alergias', tipo: 'textarea', max: 1000, largura: 'inteira' },
      { chave: 'medicamentosAtuais', rotulo: 'Medicamentos atuais', tipo: 'textarea', max: 1000, largura: 'inteira' },
    ],
  },
  clientes: {
    chave: 'clientes',
    titulo: 'Clientes',
    singular: 'cliente',
    endpoint: '/api/clientes',
    colunas: [
      { chave: 'nome', titulo: 'Nome' },
      { chave: 'email', titulo: 'E-mail' },
      { chave: 'telefone', titulo: 'Telefone' },
      { chave: 'cpf', titulo: 'CPF' },
    ],
    campos: [
      { chave: 'nome', rotulo: 'Nome', tipo: 'text', obrigatorio: true, max: 100 },
      { chave: 'email', rotulo: 'E-mail', tipo: 'email', obrigatorio: true, max: 100 },
      { chave: 'telefone', rotulo: 'Telefone', tipo: 'text', max: 20 },
      { chave: 'cpf', rotulo: 'CPF', tipo: 'text', max: 14 },
      { chave: 'endereco', rotulo: 'Endereço', tipo: 'text', max: 200, largura: 'inteira' },
      { chave: 'senha', rotulo: 'Senha (mín. 6 caracteres)', tipo: 'password', max: 72 },
    ],
  },
  agendamentos: {
    chave: 'agendamentos',
    titulo: 'Agendamentos',
    singular: 'agendamento',
    endpoint: '/api/agendamentos',
    colunas: [
      { chave: 'dataHora', titulo: 'Data e hora', formato: 'dataHora' },
      { chave: 'nomePaciente', titulo: 'Paciente' },
      { chave: 'nomeVeterinario', titulo: 'Veterinário' },
      { chave: 'motivo', titulo: 'Motivo' },
      { chave: 'status', titulo: 'Status', formato: 'status' },
    ],
    campos: [
      { chave: 'dataHora', rotulo: 'Data e hora', tipo: 'datetime', obrigatorio: true },
      {
        chave: 'pacienteId',
        rotulo: 'Paciente',
        tipo: 'select',
        obrigatorio: true,
        opcoesDe: { endpoint: '/api/pacientes', rotulo: 'nome' },
      },
      {
        chave: 'veterinarioId',
        rotulo: 'Veterinário',
        tipo: 'select',
        obrigatorio: true,
        opcoesDe: { endpoint: '/api/veterinarios', rotulo: 'nome' },
      },
      { chave: 'status', rotulo: 'Status', tipo: 'select', opcoes: STATUS },
      { chave: 'motivo', rotulo: 'Motivo', tipo: 'text', obrigatorio: true, max: 200, largura: 'inteira' },
    ],
  },
  veterinarios: {
    chave: 'veterinarios',
    titulo: 'Veterinários',
    singular: 'veterinário',
    endpoint: '/api/veterinarios',
    somenteVeterinarioEscreve: true,
    colunas: [
      { chave: 'nome', titulo: 'Nome' },
      { chave: 'email', titulo: 'E-mail' },
      { chave: 'crmv', titulo: 'CRMV' },
      { chave: 'especialidade', titulo: 'Especialidade' },
    ],
    campos: [
      { chave: 'nome', rotulo: 'Nome', tipo: 'text', obrigatorio: true, max: 100 },
      { chave: 'email', rotulo: 'E-mail', tipo: 'email', obrigatorio: true, max: 100 },
      { chave: 'telefone', rotulo: 'Telefone', tipo: 'text', max: 20 },
      { chave: 'crmv', rotulo: 'CRMV', tipo: 'text', obrigatorio: true, max: 20 },
      { chave: 'especialidade', rotulo: 'Especialidade', tipo: 'text', max: 60 },
      { chave: 'senha', rotulo: 'Senha (mín. 6 caracteres)', tipo: 'password', max: 72 },
    ],
  },
  recepcionistas: {
    chave: 'recepcionistas',
    titulo: 'Recepcionistas',
    singular: 'recepcionista',
    endpoint: '/api/recepcionistas',
    somenteVeterinarioEscreve: true,
    colunas: [
      { chave: 'nome', titulo: 'Nome' },
      { chave: 'email', titulo: 'E-mail' },
      { chave: 'telefone', titulo: 'Telefone' },
      { chave: 'turno', titulo: 'Turno' },
    ],
    campos: [
      { chave: 'nome', rotulo: 'Nome', tipo: 'text', obrigatorio: true, max: 100 },
      { chave: 'email', rotulo: 'E-mail', tipo: 'email', obrigatorio: true, max: 100 },
      { chave: 'telefone', rotulo: 'Telefone', tipo: 'text', max: 20 },
      { chave: 'turno', rotulo: 'Turno', tipo: 'text', max: 20 },
      { chave: 'senha', rotulo: 'Senha (mín. 6 caracteres)', tipo: 'password', max: 72 },
    ],
  },
};

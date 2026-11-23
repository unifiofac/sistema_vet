import { ENTIDADES } from './entidades';

describe('configuração das entidades', () => {
  it('cobre as cinco entidades do sistema', () => {
    expect(Object.keys(ENTIDADES).sort()).toEqual(
      ['agendamentos', 'clientes', 'pacientes', 'recepcionistas', 'veterinarios'],
    );
  });

  it('toda coluna e campo tem chave e rótulo', () => {
    for (const e of Object.values(ENTIDADES)) {
      expect(e.colunas.length).toBeGreaterThan(0);
      for (const c of e.campos) {
        expect(c.chave).toBeTruthy();
        expect(c.rotulo).toBeTruthy();
      }
    }
  });
});

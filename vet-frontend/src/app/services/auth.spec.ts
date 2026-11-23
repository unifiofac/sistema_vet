import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth';
import { authInterceptor } from './auth.interceptor';

function jwt(expEmSegundos: number): string {
  const payload = btoa(JSON.stringify({ sub: '1', exp: expEmSegundos }));
  return `x.${payload}.y`;
}

describe('AuthService + interceptor', () => {
  let auth: AuthService;
  let http: HttpClient;
  let backend: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'login', children: [] }]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpClient);
    backend = TestBed.inject(HttpTestingController);
  });

  it('começa sem sessão', () => {
    expect(auth.logado()).toBe(false);
    expect(auth.token()).toBeNull();
  });

  it('login guarda o usuário e o token, e as chamadas seguintes levam o Authorization', () => {
    const token = jwt(Math.floor(Date.now() / 1000) + 3600);
    let nome = '';
    auth.login('a@b.com', 'senha-qualquer').subscribe(u => (nome = u.nome));

    const login = backend.expectOne('/api/auth/login');
    expect(login.request.method).toBe('POST');
    expect(login.request.headers.has('Authorization')).toBe(false);
    login.flush({ token, usuario: { id: 1, nome: 'Dra. Ana', email: 'a@b.com', tipo: 'veterinario' } });

    expect(nome).toBe('Dra. Ana');
    expect(auth.logado()).toBe(true);

    http.get('/api/pacientes').subscribe();
    const chamada = backend.expectOne('/api/pacientes');
    expect(chamada.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    chamada.flush([]);
  });

  it('resposta 401 da API encerra a sessão', () => {
    const token = jwt(Math.floor(Date.now() / 1000) + 3600);
    auth.login('a@b.com', 'x').subscribe();
    backend.expectOne('/api/auth/login').flush({
      token,
      usuario: { id: 1, nome: 'Ana', email: 'a@b.com', tipo: 'cliente' },
    });
    expect(auth.logado()).toBe(true);

    http.get('/api/pacientes/meus').subscribe({ error: () => {} });
    backend.expectOne('/api/pacientes/meus').flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(auth.logado()).toBe(false);
  });

  it('senha errada no login (401) não derruba nada e devolve o erro', () => {
    let status = 0;
    auth.login('a@b.com', 'errada').subscribe({ error: e => (status = e.status) });
    backend.expectOne('/api/auth/login').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(status).toBe(401);
    expect(auth.logado()).toBe(false);
  });

  it('ignora sessão salva com token vencido', () => {
    const vencido = jwt(Math.floor(Date.now() / 1000) - 10);
    sessionStorage.setItem(
      'vet_sessao',
      JSON.stringify({ token: vencido, usuario: { id: 1, nome: 'X', email: 'x@y.com', tipo: 'cliente' } }),
    );
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideRouter([{ path: 'login', children: [] }]), provideHttpClient(), provideHttpClientTesting()],
    });
    expect(TestBed.inject(AuthService).logado()).toBe(false);
  });
});

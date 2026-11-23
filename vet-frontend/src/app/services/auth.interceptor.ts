import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const daApi = req.url.startsWith(`${environment.apiUrl}/api/`);
  const token = auth.token();

  const requisicao = daApi && token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(requisicao).pipe(
    catchError((erro: unknown) => {
      const ehLogin = req.url.endsWith('/api/auth/login');
      if (daApi && !ehLogin && erro instanceof HttpErrorResponse && erro.status === 401) {
        auth.logout();
      }
      return throwError(() => erro);
    }),
  );
};

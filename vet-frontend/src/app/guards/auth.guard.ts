import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const veterinarioGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const usuario = auth.usuario();

  if (!usuario) return router.createUrlTree(['/login']);
  return usuario.tipo === 'veterinario' ? true : router.createUrlTree(['/cliente']);
};

export const clienteGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const usuario = auth.usuario();

  if (!usuario) return router.createUrlTree(['/login']);
  return usuario.tipo === 'cliente' ? true : router.createUrlTree(['/veterinario']);
};

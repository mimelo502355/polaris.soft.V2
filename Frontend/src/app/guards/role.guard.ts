import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { LoginService } from '../service/login-service';
import { NotificationService } from '../service/notification.service';

/**
 * ✅ Role Guard - Solo para proteger rutas de EMPLEADO
 * NO se aplica a rutas de CLIENTE
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const loginService = inject(LoginService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  // ✅ Verificar token de EMPLEADO (no de cliente)
  const token = loginService.getToken();
  const rol = loginService.getRol();

  if (!token) {
    notificationService.error('Debes iniciar sesión como empleado.');
    router.navigate(['/login']);
    return false;
  }

  // ✅ Verificar rol requerido
  const requiredRoles = route.data['roles'] as string[];
  if (requiredRoles && !requiredRoles.includes(rol || '')) {
    notificationService.error('No tienes permisos suficientes para acceder a esta página.');
    router.navigate(['/login']);
    return false;
  }

  return true;
};
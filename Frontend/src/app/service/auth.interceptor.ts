import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoginService } from './login-service';
import { ClienteService } from './cliente.service';
import { Router } from '@angular/router';
import { NotificationService } from './notification.service';

/**
 * ✅ Auth Interceptor - Maneja AMBOS tipos de autenticación
 * - LoginService: Token de EMPLEADO
 * - ClienteService: Token de CLIENTE
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private loginService: LoginService,
    private clienteService: ClienteService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // ✅ NO agregar token en rutas de login
    if (request.url.includes('/login') || request.url.includes('/registro')) {
      return next.handle(request);
    }

    // ✅ Intentar agregar token de EMPLEADO (LoginService)
    const tokenEmpleado = this.loginService.getToken();
    if (tokenEmpleado) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${tokenEmpleado}`
        }
      });
      return next.handle(request).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.loginService.logout();
            this.router.navigate(['/login']);
            this.notificationService.error('Sesión de empleado expirada.');
          }
          if (error.status === 403) {
            this.notificationService.error('No tienes permisos de empleado.');
            this.router.navigate(['/login']);
          }
          return throwError(() => error);
        })
      );
    }

    // ✅ Si NO hay token de empleado, intentar agregar token de CLIENTE
    const tokenCliente = this.clienteService.getTokenCliente();
    if (tokenCliente) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${tokenCliente}`
        }
      });
      return next.handle(request).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.clienteService.logoutCliente();
            this.router.navigate(['/categoria']);
            this.notificationService.error('Sesión de cliente expirada.');
          }
          if (error.status === 403) {
            this.notificationService.error('No tienes permisos para esta acción.');
          }
          return throwError(() => error);
        })
      );
    }

    // ✅ Si no hay ningún token, pasar la petición sin autenticación
    return next.handle(request);
  }
}
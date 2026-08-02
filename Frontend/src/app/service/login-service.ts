import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = 'https://polaris-soft-v2.onrender.com/api/auth';

  // ✅ Keys para sessionStorage (EMPLEADO) - Prefijo "auth_" para diferenciar
  private readonly EMPLEADO_TOKEN_KEY = 'auth_token';
  private readonly EMPLEADO_ROL_KEY = 'auth_rol';
  private readonly EMPLEADO_ID_KEY = 'auth_idEmpleado';
  private readonly EMPLEADO_NOMBRE_KEY = 'auth_nombre';

  constructor(private httpClient: HttpClient) {}

  /**
   * ✅ Login empleado (nombre + contraseña)
   */
  public login(formData: any): Observable<any> {
    return this.httpClient.post(`${this.apiUrl}/login`, formData);
  }

  /**
   * ✅ Validar token con backend
   */
  public validateToken(token: string): Observable<any> {
    const headers = {
      'Authorization': `Bearer ${token}`
    };
    return this.httpClient.post(`${this.apiUrl}/validate`, {}, { headers });
  }

  /**
   * ✅ Guardar token empleado
   */
  public setToken(token: string): void {
    sessionStorage.setItem(this.EMPLEADO_TOKEN_KEY, token);
  }

  /**
   * ✅ Obtener token empleado
   */
  public getToken(): string | null {
    return sessionStorage.getItem(this.EMPLEADO_TOKEN_KEY);
  }

  /**
   * ✅ Guardar rol empleado (EMPLEADO, SUPERADMIN)
   */
  public setRol(rol: string): void {
    sessionStorage.setItem(this.EMPLEADO_ROL_KEY, rol);
  }

  /**
   * ✅ Obtener rol empleado
   */
  public getRol(): string | null {
    return sessionStorage.getItem(this.EMPLEADO_ROL_KEY);
  }

  /**
   * ✅ Guardar ID empleado
   */
  public setIdEmpleado(id: string): void {
    sessionStorage.setItem(this.EMPLEADO_ID_KEY, id);
  }

  /**
   * ✅ Obtener ID empleado
   */
  public getIdEmpleado(): string | null {
    return sessionStorage.getItem(this.EMPLEADO_ID_KEY);
  }

  /**
   * ✅ Guardar nombre empleado
   */
  public setNombre(nombre: string): void {
    sessionStorage.setItem(this.EMPLEADO_NOMBRE_KEY, nombre);
  }

  /**
   * ✅ Obtener nombre empleado
   */
  public getNombre(): string | null {
    return sessionStorage.getItem(this.EMPLEADO_NOMBRE_KEY);
  }

  /**
   * ✅ Logout empleado - Limpia TODOS los datos
   */
  public logout(): void {
    sessionStorage.removeItem(this.EMPLEADO_TOKEN_KEY);
    sessionStorage.removeItem(this.EMPLEADO_ROL_KEY);
    sessionStorage.removeItem(this.EMPLEADO_ID_KEY);
    sessionStorage.removeItem(this.EMPLEADO_NOMBRE_KEY);
  }

  /**
   * ✅ Verificar si empleado está logueado
   */
  public isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
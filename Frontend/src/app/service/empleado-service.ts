import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Empleado {
  idEmpleado?: string;
  nombre: string;
  password?: string;
  rol?: string;
  estado?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {
  private apiUrl = 'http://localhost:8081/empleado';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.apiUrl}/getall`);
  }

  insert(empleado: Empleado): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/insert`, empleado);
  }

  update(empleado: Empleado): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update`, empleado);
  }

  cambiarEstado(id: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/desactivar/${id}`, {});
  }
}
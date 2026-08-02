import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {

  private apiUrl = 'http://localhost:8081/reportes/excel';

  constructor(private http: HttpClient) {}

  descargarReporteExcel(tipo: 'empleados' | 'productos' | 'ventas'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${tipo}`, {
      responseType: 'blob'
    });
  }
}
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductoAcompañamientoService {
   private baseUrl = 'https://polaris-soft-v2.onrender.com/producto-acompanamiento';

  constructor(private http: HttpClient) {}


  getByProducto(idProducto: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/getbyproducto/${idProducto}`);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// En acompañamiento-service.ts
@Injectable({
  providedIn: 'root',
})
export class AcompanamientoService {
  constructor(private httpClient: HttpClient) {}

 
  public getAllTopping(): Observable<any> {
    return this.httpClient.get('https://polaris-soft-v2.onrender.com/acompanamiento/topping');
  }

  public getAllSalsas(): Observable<any> {
    return this.httpClient.get('https://polaris-soft-v2.onrender.com/acompanamiento/salsas');
  }

  public getByProducto(idProducto: string): Observable<any[]> {
    return this.httpClient.get<any[]>(
      `https://polaris-soft-v2.onrender.com/producto-acompanamiento/getbyproducto/${idProducto}`
    );
  }
}
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class EstadisticaService {
  constructor(
		private httpClient: HttpClient
	) {}

  public ventasSemana():Observable<any> {
    return this.httpClient.get<any[]>('https://polaris-soft-v2.onrender.com/estadisticas/ventas-semana');
  }

  public productoEstrella(inicio: string, fin: string) {
    return this.httpClient.get<any[]>( `https://polaris-soft-v2.onrender.com/estadisticas/producto-estrella?inicio=${inicio}&fin=${fin}`);
  }

}

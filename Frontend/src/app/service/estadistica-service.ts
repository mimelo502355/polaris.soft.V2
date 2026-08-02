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
    return this.httpClient.get<any[]>('http://localhost:8081/estadisticas/ventas-semana');
  }

  public productoEstrella(inicio: string, fin: string) {
    return this.httpClient.get<any[]>( `http://localhost:8081/estadisticas/producto-estrella?inicio=${inicio}&fin=${fin}`);
  }

}

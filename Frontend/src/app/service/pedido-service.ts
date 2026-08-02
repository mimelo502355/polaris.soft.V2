import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PedidoService {

  private baseUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  /**
   * ✅ Insertar nuevo pedido
   */
  insert(pedido: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/pedido/insert`, pedido, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  /**
   * ✅ Cambiar estado del pedido
   */
  cambioEstado(idPedido: string, estado: string): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/pedido/estado/${idPedido}`,
      { estado }
    );
  }

  /**
   * ✅ Actualizar un pedido
   */
  update(idPedido: string, pedido: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/pedido/${idPedido}`, pedido);
  }

  /**
   * ✅ Obtener estado actual del pedido
   */
  getEstadoPedido(idPedido: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/pedido/estado/${idPedido}`);
  }

  /**
   * ✅ Obtener todos los pedidos
   */
  getAll(): Observable<any> {
    return this.http.get(`${this.baseUrl}/pedido/getall`);
  }

  /**
   * ✅ CORREGIDO - Obtener detalle del pedido CON ACOMPAÑAMIENTOS
   * CAMBIO: Ahora llama a /pedidoItem/pedido/{idPedido}
   * que devuelve los items con el campo "acompanamientos"
   */
  getDetallePedido(idPedido: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/pedidoItem/pedido/${idPedido}`);
  }

  /**
   * ✅ Solicitar cancelación de un pedido
   */
  solicitarCancelacion(idPedido: string, motivoCancelacion: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/pedido/${idPedido}/solicitar-cancelacion`, {
      motivoCancelacion
    });
  }

  /**
   * ✅ Responder a solicitud de cancelación
   */
  responderCancelacion(idPedido: string, aceptar: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/pedido/${idPedido}/responder-cancelacion`, {
      aceptar
    });
  }

  /**
   * ✅ Finalizar un pedido
   */
  finalizarPedido(idPedido: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/pedido/finalizar`, { idPedido });
  }
}
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * ✅ INTERFACE DtoPedidoItem - DEBE COINCIDIR CON EL BACKEND
 */
export interface DtoPedidoItem {
  idItem: string;
  idProducto: string;
  idPedido: string;
  cantidad: number;
  precioUnitarioFinal: number;
  acompanamientos?: string;  // ✅ AGREGADO
  toppings?: string[];
  salsas?: string[];
  toppingsIds?: string[];
  salsasIds?: string[];
  nombreProducto?: string;
  imagenProducto?: string;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface PedidoItemAcompanamiento {
  id: string;
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class PedidoItemService {

  private apiUrl = 'https://polaris-soft-v2.onrender.com/pedidoItem';

  constructor(private http: HttpClient) { }

  /**
   * ✅ Obtener todos los items de un pedido específico CON ACOMPAÑAMIENTOS
   * @param idPedido ID del pedido
   */
  getByPedidoId(idPedido: string): Observable<DtoPedidoItem[]> {
    return this.http.get<DtoPedidoItem[]>(`${this.apiUrl}/pedido/${idPedido}`);
  }

  /**
   * ✅ Obtener todos los items de todos los pedidos
   */
  getAll(): Observable<DtoPedidoItem[]> {
    return this.http.get<DtoPedidoItem[]>(`${this.apiUrl}/getall`);
  }

  /**
   * ✅ INSERTAR ITEM - ENVÍA JSON PURO (NO FormData)
   * @param idPedido ID del pedido
   * @param dtoPedidoItem Objeto DTO del item
   */
  insertItem(idPedido: string, dtoPedidoItem: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    console.log('📤 PedidoItemService.insertItem() - Enviando:');
    console.log('   URL:', `${this.apiUrl}/insert/${idPedido}`);
    console.log('   DTO:', dtoPedidoItem);

    return this.http.post(
      `${this.apiUrl}/insert/${idPedido}`,
      dtoPedidoItem,
      { headers }
    );
  }

  /**
   * ✅ ALIAS para compatibilidad - llama a insertItem()
   */
  insert(idPedido: string, dtoPedidoItem: any): Observable<any> {
    return this.insertItem(idPedido, dtoPedidoItem);
  }

  /**
   * 📊 Obtener información formateada de acompañamientos
   * @param item Item del pedido
   */
  getAcompanamientosFormateados(item: DtoPedidoItem): string {
    const partes: string[] = [];

    if (item.toppings && item.toppings.length > 0) {
      partes.push(`Toppings: ${item.toppings.join(', ')}`);
    }

    if (item.salsas && item.salsas.length > 0) {
      partes.push(`Salsas: ${item.salsas.join(', ')}`);
    }

    return partes.length > 0 ? partes.join(' | ') : 'Sin acompañamientos';
  }

  /**
   * 🎯 Verificar si un item tiene acompañamientos
   * @param item Item del pedido
   */
  tieneAcompanamientos(item: DtoPedidoItem): boolean {
    const tieneToppings = item.toppings && item.toppings.length > 0;
    const tieneSalsas = item.salsas && item.salsas.length > 0;
    return !!(tieneToppings || tieneSalsas);  // ✅ CORREGIDO: Devuelve boolean explícitamente
  }

  /**
   * 📋 Contar total de acompañamientos
   * @param item Item del pedido
   */
  contarAcompanamientos(item: DtoPedidoItem): number {
    const countToppings = item.toppings ? item.toppings.length : 0;
    const countSalsas = item.salsas ? item.salsas.length : 0;
    return countToppings + countSalsas;
  }

  /**
   * 🔄 Construir FormData para agregar item con acompañamientos
   * @param idProducto ID del producto
   * @param cantidad Cantidad
   * @param precio Precio unitario
   * @param toppingIds Array de IDs de toppings
   * @param salsaId Array de IDs de salsas
   */
  crearFormData(
    idProducto: string,
    cantidad: number,
    precio: string,
    toppingIds?: string[],
    salsaId?: string[]
  ): FormData {
    const formData = new FormData();
    formData.append('dto.pedidoItem.idProducto', idProducto);
    formData.append('dto.pedidoItem.cantidad', cantidad.toString());
    formData.append('dto.pedidoItem.precioUnitarioFinal', precio);

    // Agregar toppings
    if (toppingIds && toppingIds.length > 0) {
      toppingIds.forEach(id => {
        formData.append('dto.pedidoItem.toppings', id);
      });
    }

    // Agregar salsas
    if (salsaId && salsaId.length > 0) {
      salsaId.forEach(id => {
        formData.append('dto.pedidoItem.salsas', id);
      });
    }

    return formData;
  }
}
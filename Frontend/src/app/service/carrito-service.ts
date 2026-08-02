import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CarritoService {
  private items: any[] = [];

  /**
   * ✅ AGREGAR PRODUCTO AL CARRITO CON TOPPINGS Y SALSAS
   */
  agregar(producto: any) {
    console.log('📥 CarritoService.agregar() - Producto recibido:', producto);

    // ✅ Normalizar toppings y salsas (pueden ser strings o objetos)
    const salsas = this.normalizarAcompanamientos(producto.salsas || []);
    const toppings = this.normalizarAcompanamientos(producto.toppings || []);

    console.log('  → Toppings normalizados:', toppings);
    console.log('  → Salsas normalizadas:', salsas);

    // ✅ Buscar si existe el mismo producto CON los mismos acompañamientos
    const index = this.items.findIndex(
      p =>
        p.idProducto === producto.idProducto &&
        JSON.stringify(p.salsas || []) === JSON.stringify(salsas) &&
        JSON.stringify(p.toppings || []) === JSON.stringify(toppings)
    );

    // ✅ Si existe, aumentar cantidad. Si no, agregar nuevo item
    if (index >= 0) {
      this.items[index].cantidad++;
      console.log('✅ Cantidad aumentada para producto:', producto.idProducto);
    } else {
      this.items.push({
        ...producto,
        salsas: salsas,
        toppings: toppings,
        cantidad: 1
      });
      console.log('✅ Nuevo producto agregado al carrito');
    }

    console.log('🛒 Carrito actualizado:', this.items);
  }

  /**
   * ✅ NORMALIZAR ACOMPAÑAMIENTOS
   * Convierte objetos con {id, nombre} en solo strings con nombres
   */
  private normalizarAcompanamientos(arr: any[]): string[] {
    if (!arr || arr.length === 0) {
      return [];
    }

    return arr.map(item => {
      // Si es string, retorna como está
      if (typeof item === 'string') {
        return item;
      }
      // Si es objeto, extrae el nombre
      return item.nombre || item.name || String(item);
    });
  }

  /**
   * ✅ OBTENER ITEMS DEL CARRITO
   */
  obtenerItems() {
    return this.items;
  }

  /**
   * ✅ AUMENTAR CANTIDAD
   */
  aumentar(index: number) {
    if (index >= 0 && index < this.items.length) {
      this.items[index].cantidad++;
    }
  }

  /**
   * ✅ DISMINUIR CANTIDAD
   */
  disminuir(index: number) {
    if (index >= 0 && index < this.items.length) {
      if (this.items[index].cantidad > 1) {
        this.items[index].cantidad--;
      }
    }
  }

  /**
   * ✅ ELIMINAR ITEM
   */
  eliminar(index: number) {
    if (index >= 0 && index < this.items.length) {
      this.items.splice(index, 1);
    }
  }

  /**
   * ✅ LIMPIAR CARRITO
   */
  limpiar() {
    this.items = [];
  }

  /**
   * ✅ CALCULAR TOTAL
   */
  total() {
    return this.items.reduce(
      (sum, item) => sum + (item.precioBase * item.cantidad),
      0
    );
  }
}
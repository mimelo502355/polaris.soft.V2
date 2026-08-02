import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CarritoService } from '../../service/carrito-service';
import { ClienteService } from '../../service/cliente.service';
import { ModalClienteService } from '../../service/modal-cliente.service';
import { NotificationService } from '../../service/notification.service';

/**
 * ✅ Carrito Flotante - Widget en esquina inferior derecha
 * Muestra items y total. Permite sumar/restar cantidades
 */
@Component({
  selector: 'app-carrito-flotante',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './carrito-flotante.component.html',
  styleUrl: './carrito-flotante.component.css'
})
export class CarritoFlotanteComponent implements OnInit, OnDestroy {

  items: any[] = [];
  mostrarDetalle = false;
  actualizacionInterval: any;

  constructor(
    private carritoService: CarritoService,
    private clienteService: ClienteService,
    private modalClienteService: ModalClienteService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.actualizarCarrito();
    
    // ✅ Actualizar carrito cada 500ms
    this.actualizacionInterval = setInterval(() => {
      this.actualizarCarrito();
    }, 500);
  }

  ngOnDestroy(): void {
    if (this.actualizacionInterval) {
      clearInterval(this.actualizacionInterval);
    }
  }

  /**
   * Obtener items del carrito
   */
  actualizarCarrito(): void {
    this.items = this.carritoService.obtenerItems();
  }

  /**
   * Cantidad total de items
   */
  getCantidadTotal(): number {
    return this.items.reduce((sum, item) => sum + item.cantidad, 0);
  }

  /**
   * Total a pagar
   */
  getTotal(): number {
    return this.carritoService.total();
  }

  /**
   * ✅ AUMENTAR cantidad de un item
   */
  aumentar(index: number): void {
    if (index >= 0 && index < this.items.length) {
      this.carritoService.aumentar(index);
      this.actualizarCarrito();
    }
  }

  /**
   * ✅ DISMINUIR cantidad de un item
   */
  disminuir(index: number): void {
    if (index >= 0 && index < this.items.length) {
      // Si la cantidad es 1, eliminar el item
      if (this.items[index].cantidad === 1) {
        this.eliminar(index);
      } else {
        this.carritoService.disminuir(index);
        this.actualizarCarrito();
      }
    }
  }

  /**
   * ✅ ELIMINAR item del carrito
   */
  eliminar(index: number): void {
    if (index >= 0 && index < this.items.length) {
      const itemNombre = this.items[index].nombre;
      this.carritoService.eliminar(index);
      this.actualizarCarrito();
      this.notificationService.success(`${itemNombre} eliminado del carrito`);
    }
  }

  /**
   * ✅ Ir a pedido - Verifica login antes
   */
  irAPedido(): void {
    // Si NO está logueado, abrir modal
    if (!this.clienteService.isClienteLogueado()) {
      this.modalClienteService.abrirModal();
      this.notificationService.warning('Por favor inicia sesión para continuar');
      return;
    }

    // Si carrito está vacío
    if (this.items.length === 0) {
      this.notificationService.warning('El carrito está vacío');
      return;
    }

    // Navegar a /pedido
    setTimeout(() => {
      this.router.navigate(['/pedido']);
      this.mostrarDetalle = false;
    }, 300);
  }

  /**
   * Volver a categorías
   */
  volverACategorias(): void {
    this.router.navigate(['/categoria']);
    this.mostrarDetalle = false;
  }

  /**
   * Toggle panel detalle
   */
  toggleDetalle(): void {
    this.mostrarDetalle = !this.mostrarDetalle;
  }

  /**
   * Cerrar panel
   */
  cerrarPanel(): void {
    this.mostrarDetalle = false;
  }
}
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoMonitorService } from '../../service/pedido-monitor.service';
import { PedidoService } from '../../service/pedido-service';
import { NotificationService } from '../../service/notification.service';

/**
 * ✅ Modal GLOBAL - Pedido LISTO a pantalla completa
 * Aparece desde CUALQUIER PÁGINA
 */
@Component({
  selector: 'app-modal-pedido-listo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-pedido-listo.component.html',
  styleUrl: './modal-pedido-listo.component.css'
})
export class ModalPedidoListoComponent implements OnInit, OnDestroy {

  mostrarModal = false;
  pedidoListo: any = null;
  cargando = false;
  subscription: any;

  constructor(
    private pedidoMonitorService: PedidoMonitorService,
    private pedidoService: PedidoService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // ✅ Escuchar pedidos LISTO desde cualquier página
    this.subscription = this.pedidoMonitorService.pedidoListo$.subscribe((pedido: any) => {
      if (pedido) {
        this.mostrarPedidoListo(pedido);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  /**
   * ✅ Mostrar modal a pantalla completa
   */
  mostrarPedidoListo(pedido: any): void {
    this.pedidoListo = pedido;
    this.mostrarModal = true;

    // Reproducir sonido
    this.reproducirSonido();

    // Toast
    this.notificationService.success(
      '🎉 ¡Tu pedido está listo! Presiona Aceptar'
    );
  }

  /**
   * ✅ Aceptar pedido LISTO
   */
  aceptarPedidoListo(): void {
    if (!this.pedidoListo) return;

    this.cargando = true;

    this.pedidoService.cambioEstado(
      this.pedidoListo.idPedido,
      'FINALIZADO'
    ).subscribe({
      next: () => {
        this.cargando = false;
        this.mostrarModal = false;

        this.notificationService.success('Pedido finalizado');

        setTimeout(() => {
          this.pedidoMonitorService.resetear();
        }, 500);
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error:', err);
        this.notificationService.error('Error al finalizar');
      }
    });
  }

  /**
   * Reproducir sonido
   */
  reproducirSonido(): void {
    try {
      const audio = new Audio('data:audio/wav;base64,UklGRiYAAABXQVZFZm10IBAAAAABAAEAQB8AAAB9AAACABAAZGF0YQIAAAAAAA==');
      audio.play().catch(() => {});
    } catch (e) {}
  }

  /**
   * Cerrar modal
   */
  cerrar(): void {
    this.mostrarModal = false;
  }
}
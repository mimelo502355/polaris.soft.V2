import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { PedidoService } from '../../service/pedido-service';
import { PedidoMonitorService } from '../../service/pedido-monitor.service';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-historial-flotante',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './historial-flotante.component.html',
  styleUrl: './historial-flotante.component.css'
})
export class HistorialFlotanteComponent implements OnInit, OnDestroy {
  mostrarModal = false;
  pedidosEnProceso: any[] = [];
  pedidosRealizados: any[] = [];
  loading = false;
  activeTab: 'proceso' | 'realizados' = 'proceso';
  recargarInterval: any;

  // ✅ Modal LISTO por recoger
  mostrarModalListo = false;
  pedidoListoActual: any = null;
  cargandoAceptar = false;

  constructor(
    public clienteService: ClienteService,
    private pedidoService: PedidoService,
    private pedidoMonitorService: PedidoMonitorService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if (this.clienteService.isClienteLogueado()) {
      this.cargarPedidos();
      this.recargarInterval = setInterval(() => {
        if (this.mostrarModal) {
          this.cargarPedidos();
        }
      }, 10000);

      // ✅ Escuchar notificaciones de LISTO
      this.pedidoMonitorService.pedidoListo$.subscribe((pedido: any) => {
        if (pedido) {
          this.mostrarNotificacionListo(pedido);
        }
      });
    }
  }

  ngOnDestroy(): void {
    if (this.recargarInterval) {
      clearInterval(this.recargarInterval);
    }
  }

  cargarPedidos(): void {
    this.loading = true;
    const nombreCliente = this.clienteService.getNombres();

    if (!nombreCliente) {
      this.loading = false;
      return;
    }

    this.pedidoService.getAll().subscribe({
      next: (response: any) => {
        this.loading = false;
        const pedidosCliente = response.filter(
          (p: any) => p.nombreCliente === nombreCliente
        );

        // ✅ Pedidos en proceso
        this.pedidosEnProceso = pedidosCliente.filter((p: any) =>
          ['ESPERA', 'PREPARACION', 'PREPARANDO'].includes(p.estado)
        );

        // ✅ CORREGIDO: Cambiar COMPLETADO por FINALIZADO
        this.pedidosRealizados = pedidosCliente.filter((p: any) =>
          ['LISTO', 'FINALIZADO', 'CANCELADO'].includes(p.estado)
        );

        console.log('📋 Pedidos en proceso:', this.pedidosEnProceso.length);
        console.log('✅ Pedidos realizados:', this.pedidosRealizados.length);
      },
      error: (err) => {
        this.loading = false;
        console.error('Error al cargar pedidos', err);
      }
    });
  }

  /**
   * ✅ Mostrar notificación SOLO si estado es LISTO
   */
  mostrarNotificacionListo(pedido: any): void {
    // ✅ Verificación extra: SOLO para LISTO
    if (pedido.estado !== 'LISTO') {
      console.log('Ignorando pedido que no es LISTO:', pedido.estado);
      return;
    }

    this.pedidoListoActual = pedido;
    this.mostrarModalListo = true;

    // Toast
    this.notificationService.success(
      '🎉 ¡Tu pedido está listo! Acércate al mostrador'
    );

    // Reproducir sonido
    this.reproducirSonido();
  }

  /**
   * ✅ Aceptar pedido LISTO y cambiar a FINALIZADO
   */
  aceptarPedidoListo(): void {
    if (!this.pedidoListoActual) return;

    this.cargandoAceptar = true;

    this.pedidoService.cambioEstado(
      this.pedidoListoActual.idPedido,
      'FINALIZADO'
    ).subscribe({
      next: () => {
        this.cargandoAceptar = false;
        this.mostrarModalListo = false;
        this.notificationService.success('Pedido finalizado correctamente');
        setTimeout(() => {
          this.cargarPedidos();
          this.pedidoMonitorService.resetear();
        }, 1000);
      },
      error: (err) => {
        this.cargandoAceptar = false;
        console.error('Error:', err);
        this.notificationService.error('Error al finalizar pedido');
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

  getEstadoColor(estado: string): string {
    const colores: any = {
      'ESPERA': '#ffc107',
      'PREPARACION': '#ff9800',
      'PREPARANDO': '#ff9800',
      'LISTO': '#4caf50',
      'FINALIZADO': '#2196f3',
      'CANCELADO': '#f44336'
    };
    return colores[estado] || '#999';
  }

  getEstadoIcon(estado: string): string {
    const iconos: any = {
      'ESPERA': '⏳',
      'PREPARACION': '👨‍🍳',
      'PREPARANDO': '👨‍🍳',
      'LISTO': '✅',
      'FINALIZADO': '🎉',
      'CANCELADO': '❌'
    };
    return iconos[estado] || '•';
  }

  verDetalle(pedido: any): void {
    this.router.navigate(['/estado-pedido']);
    this.mostrarModal = false;
  }

  getCantidadEnProceso(): number {
    return this.pedidosEnProceso.length;
  }

  toggleModal(): void {
    if (!this.clienteService.isClienteLogueado()) {
      this.notificationService.error('Debes iniciar sesión');
      return;
    }

    this.mostrarModal = !this.mostrarModal;
    if (this.mostrarModal) {
      this.cargarPedidos();
    }
  }

  cambiarTab(tab: 'proceso' | 'realizados'): void {
    this.activeTab = tab;
  }

  // ✅ NUEVAS PROPIEDADES
  mostrarModalDetalle = false;
  pedidoDetalle: any = null;

  /**
   * ✅ Ver detalle del pedido (modal, no navegar)
   * ✨ MEJORADO: Ahora carga los items/productos del pedido
   */
  verDetallePedido(pedido: any): void {
    this.pedidoDetalle = pedido;
    
    // ✅ NUEVO: Obtener items del pedido si no existen
    if (pedido.idPedido && (!pedido.items || pedido.items.length === 0)) {
      console.log('📦 Cargando items para pedido:', pedido.idPedido);
      this.pedidoService.getDetallePedido(pedido.idPedido).subscribe({
        next: (items: any) => {
          this.pedidoDetalle.items = items;
          console.log('✅ Items cargados exitosamente:', items);
        },
        error: (err) => {
          console.error('❌ Error al cargar items:', err);
          this.pedidoDetalle.items = [];
        }
      });
    }
    
    this.mostrarModalDetalle = true;
  }

  /**
   * ✅ Cerrar modal detalle
   */
  cerrarModalDetalle(): void {
    this.mostrarModalDetalle = false;
  }

  /**
   * Obtener descripción del estado
   */
  getDescripcionEstado(estado: string): string {
    const descripciones: any = {
      'LISTO': 'Tu pedido fue completado y está listo para recoger',
      'FINALIZADO': 'Pedido recolectado exitosamente',
      'CANCELADO': 'Este pedido fue cancelado'
    };
    return descripciones[estado] || 'Pedido procesado';
  }
}
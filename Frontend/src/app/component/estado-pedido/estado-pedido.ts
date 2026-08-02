import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { PedidoService } from '../../service/pedido-service';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-estado-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './estado-pedido.html',
  styleUrl: './estado-pedido.css'
})
export class EstadoPedidoComponent implements OnInit, OnDestroy {

  pedidosActivos: any[] = [];
  pedidoSeleccionado: any = null;
  cargando = false;
  cancelacionRechazada = false;

  mostrarModalCancelacion = false;
  mostrarModalExito = false;
  motivoCancelacion = '';

  intervalId: any;
  nombreCliente: string = '';

  constructor(
    private pedidoService: PedidoService,
    private clienteService: ClienteService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.nombreCliente = this.clienteService.getNombres() || '';

    if (!this.nombreCliente) {
      this.router.navigate(['/categoria']);
      return;
    }

    this.cargarPedidosDelCliente();

    this.intervalId = setInterval(() => {
      this.cargarPedidosDelCliente();
    }, 3000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  cargarPedidosDelCliente(): void {
    this.cargando = true;

    this.pedidoService.getAll().subscribe({
      next: (response: any) => {
        this.cargando = false;

        const pedidosCliente = response.filter(
          (p: any) => p.nombreCliente === this.nombreCliente
        );

        this.pedidosActivos = pedidosCliente.filter((p: any) => {
          const estadoNormalizado = (p.estado || '').toUpperCase().trim();
          
          const estadosActivos = [
            'ESPERA',
            'PREPARACION',
            'PREPARANDO',
            'SOLICITUD_CANCELACION'
          ];
          
          return estadosActivos.includes(estadoNormalizado);
        });

        if (this.pedidosActivos.length === 0) {
          this.pedidoSeleccionado = null;
        }

        if (!this.pedidoSeleccionado && this.pedidosActivos.length > 0) {
          this.pedidoSeleccionado = this.pedidosActivos[0];
          this.verificarCancelacionRechazada();
        } else if (this.pedidoSeleccionado) {
          const pedidoActualizado = this.pedidosActivos.find(
            (p: any) => p.idPedido === this.pedidoSeleccionado.idPedido
          );
          
          if (pedidoActualizado) {
            this.pedidoSeleccionado = pedidoActualizado;
            this.verificarCancelacionRechazada();
          } else {
            if (this.pedidosActivos.length > 0) {
              this.pedidoSeleccionado = this.pedidosActivos[0];
              this.verificarCancelacionRechazada();
            } else {
              this.pedidoSeleccionado = null;
            }
          }
        }
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error al cargar pedidos', err);
      }
    });
  }

  verificarCancelacionRechazada(): void {
    if (!this.pedidoSeleccionado) {
      this.cancelacionRechazada = false;
      return;
    }

    const estadoNormalizado = (this.pedidoSeleccionado.estado || '').toUpperCase().trim();
    this.cancelacionRechazada = estadoNormalizado === 'PREPARACION' || estadoNormalizado === 'PREPARANDO';
  }

  seleccionarPedido(pedido: any): void {
    this.pedidoSeleccionado = pedido;
    this.verificarCancelacionRechazada();
  }

  getEstadoColor(estado: string): string {
    const colores: any = {
      'ESPERA': '#ffc107',
      'PREPARACION': '#ff9800',
      'PREPARANDO': '#ff9800',
      'SOLICITUD_CANCELACION': '#e74c3c'
    };
    return colores[estado] || '#999';
  }

  getEstadoIcon(estado: string): string {
    const iconos: any = {
      'ESPERA': '⏳',
      'PREPARACION': '👨‍🍳',
      'PREPARANDO': '👨‍🍳',
      'SOLICITUD_CANCELACION': '❌'
    };
    return iconos[estado] || '•';
  }

  abrirModalCancelacion(): void {
    this.motivoCancelacion = '';
    this.mostrarModalCancelacion = true;
  }

  cerrarModalCancelacion(): void {
    this.mostrarModalCancelacion = false;
  }

  solicitarCancelacion(): void {
    if (!this.pedidoSeleccionado) return;

    this.cargando = true;
    this.pedidoService.solicitarCancelacion(
      this.pedidoSeleccionado.idPedido,
      this.motivoCancelacion
    ).subscribe({
      next: () => {
        this.cargando = false;
        this.mostrarModalCancelacion = false;
        this.pedidoSeleccionado.estado = 'SOLICITUD_CANCELACION';
        this.mostrarModalExito = true;

        setTimeout(() => {
          this.mostrarModalExito = false;
          this.cargarPedidosDelCliente();
        }, 2000);
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error al solicitar cancelación:', err);
        alert('No se pudo enviar la solicitud de cancelación.');
      }
    });
  }

  verCategorias(): void {
    this.router.navigate(['/categoria']);
  }

  volverAlInicio(): void {
    sessionStorage.removeItem('idPedido');
    this.router.navigate(['/categoria']);
  }

  getCantidadPedidos(): number {
    return this.pedidosActivos.length;
  }
}
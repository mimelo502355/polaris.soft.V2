import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService } from '../../service/pedido-service';

@Component({
  selector: 'app-panel-empleado',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panel-empleado.html',
  styleUrl: './panel-empleado.css',
})
export class PanelEmpleado implements OnInit, OnDestroy {
  pedidos: any[] = [];
  cargando = false;
  pedidoSeleccionado: any = null;
  modalVisible = false;
  intervalId: any;
  
  // Set para almacenar IDs aceptados localmente y sobrevivir al polling de 4s
  pedidosAceptados: Set<any> = new Set();
  
  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.cargarPedido(true);
    
    // Polling cada 4 segundos para ver nuevos pedidos y cancelaciones
    this.intervalId = setInterval(() => {
      this.cargarPedido(false);
    }, 4000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  /**
   * ✅ Cargar pedidos - FILTRAR para NO mostrar FINALIZADO
   */
  cargarPedido(esCargaInicial: boolean = false) {
    if (esCargaInicial) {
      this.cargando = true;
    }
    this.pedidoService.getAll().subscribe({
      next: (res: any) => {
        // ✅ FILTRAR: Solo mostrar pedidos que NO sean FINALIZADO, COMPLETADO, etc
        this.pedidos = res.filter((p: any) => {
          const estado = (p.estado || '').toUpperCase().trim();
          
          const estadosValidos = [
            'ESPERA',
            'PREPARACION',
            'PREPARANDO',
            'LISTO',
            'SOLICITUD_CANCELACION',
            'CANCELADO'
          ];
          
          return estadosValidos.includes(estado);
        });
        
        // Cargar detalles de cada pedido
        this.pedidos.forEach((p: any) => {
          p.mostrarDetalle = false;
          
          if (!p.items || p.items.length === 0) {
            this.pedidoService.getDetallePedido(p.idPedido).subscribe({
              next: (detalle: any) => {
                p.items = Array.isArray(detalle) ? detalle : (detalle.items || []); 
              },
              error: (err: any) => {
                console.error('Error al cargar detalle del pedido', p.idPedido, err);
                p.items = []; 
              }
            });
          }
        });
        this.cargando = false;
      },
      error: (err: any) => {
        if (esCargaInicial) {
          alert('Error al cargar pedidos');
        }
        console.error('Error en getAll:', err);
        this.cargando = false;
      }
    });
  }

  verDetalle(pedido: any) {
    this.pedidoSeleccionado = pedido;
    this.modalVisible = true;
    
    if (!pedido.items || pedido.items.length === 0) {
      this.cargarDetalle(pedido);
    }
  }

  cerrarModal() {
    this.modalVisible = false;
    setTimeout(() => {
      this.pedidoSeleccionado = null;
    }, 300);
  }

  cargarDetalle(pedido: any) {
    this.pedidoService.getDetallePedido(pedido.idPedido).subscribe({
      next: (detalle: any) => {
        const items = Array.isArray(detalle) ? detalle : (detalle.items || []);
        pedido.items = items;
        
        if (this.pedidoSeleccionado && this.pedidoSeleccionado.idPedido === pedido.idPedido) {
          this.pedidoSeleccionado.items = items;
        }
      },
      error: (err: any) => {
        console.error('Error al cargar detalle del pedido', pedido.idPedido, err);
        pedido.items = []; 
      }
    });
  }

  /**
   * ✅ Cambiar estado del pedido
   * Cuando cambia a FINALIZADO, se elimina automáticamente del panel
   */
  cambiarEstado(pedido: any, nuevoEstado: string) {
    this.pedidoService.cambioEstado(pedido.idPedido, nuevoEstado)
      .subscribe({
        next: () => {
          pedido.estado = nuevoEstado;
          
          if (this.pedidoSeleccionado && this.pedidoSeleccionado.idPedido === pedido.idPedido) {
            this.pedidoSeleccionado.estado = nuevoEstado;
          }
          
          // ✅ Si cambió a FINALIZADO, cerrar modal y recargar
          if (nuevoEstado === 'FINALIZADO') {
            this.cerrarModal();
            setTimeout(() => {
              this.cargarPedido(false);
            }, 500);
          }
          // ✅ Si cambió a LISTO, cerrar modal después de 1.5s
          else if (nuevoEstado === 'LISTO' && this.modalVisible) {
            setTimeout(() => {
              this.cerrarModal();
            }, 1500);
          }
        },
        error: (error: any) => {
          console.error('Error al cambiar estado:', error);
          alert('Error al cambiar el estado del pedido');
        }
      });
  }

  aceptarCancelacionLocal(pedido: any) {
    if (pedido?.idPedido) {
      this.pedidosAceptados.add(pedido.idPedido);
    }
  }

  cancelarConfirmacion(pedido: any) {
    if (pedido?.idPedido) {
      this.pedidosAceptados.delete(pedido.idPedido);
    }
  }

  estaAceptada(pedido: any): boolean {
    return !!pedido?.idPedido && this.pedidosAceptados.has(pedido.idPedido);
  }

  responderCancelacion(pedido: any, aceptar: boolean) {
    this.pedidoService.responderCancelacion(pedido.idPedido, aceptar).subscribe({
      next: () => {
        if (pedido?.idPedido) {
          this.pedidosAceptados.delete(pedido.idPedido);
        }
        this.cargarPedido(false);
        if (this.modalVisible && this.pedidoSeleccionado?.idPedido === pedido.idPedido) {
          this.cerrarModal();
        }
      },
      error: (err: any) => {
        console.error('Error al responder la cancelación:', err);
        alert('No se pudo procesar la respuesta de cancelación.');
      }
    });
  }

  getEstadoIcon(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'ESPERA': return 'bi-clock';
      case 'PREPARACION': return 'bi-egg-fried';
      case 'PREPARANDO': return 'bi-egg-fried';
      case 'SOLICITUD_CANCELACION': return 'bi-exclamation-triangle-fill';
      case 'CANCELADO': return 'bi-x-circle-fill';
      case 'LISTO': return 'bi-check-circle';
      default: return 'bi-question-circle';
    }
  }
  
  getEstadoClass(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'ESPERA': return 'estado-espera';
      case 'PREPARACION': return 'estado-preparacion';
      case 'PREPARANDO': return 'estado-preparacion';
      case 'SOLICITUD_CANCELACION': return 'estado-solicitud-cancelacion';
      case 'CANCELADO': return 'estado-cancelado';
      case 'LISTO': return 'estado-listo';
      default: return 'estado-default';
    }
  }
  
  /**
   * ✅ GETTERS MEJORADOS
   */
  get pedidosNuevos() {
    return this.pedidos.filter(p => {
      const estado = (p.estado || '').toUpperCase().trim();
      return ['ESPERA', 'PREPARACION', 'PREPARANDO', 'SOLICITUD_CANCELACION'].includes(estado);
    });
  }

  get pedidosEnPreparacion() {
    return this.pedidos.filter(p => {
      const estado = (p.estado || '').toUpperCase().trim();
      return ['PREPARACION', 'PREPARANDO'].includes(estado);
    });
  }

  get pedidosListos() {
    return this.pedidos.filter(p => {
      const estado = (p.estado || '').toUpperCase().trim();
      return estado === 'LISTO';
    });
  }

  get pedidosCancelados() {
    return this.pedidos.filter(p => {
      const estado = (p.estado || '').toUpperCase().trim();
      return estado === 'CANCELADO';
    });
  }

  get pedidosPendientes() {
    return this.pedidos.filter(p => {
      const estado = (p.estado || '').toUpperCase().trim();
      return estado === 'SOLICITUD_CANCELACION';
    });
  }
  
  /**
   * ✅ Total de pedidos activos (NO incluye FINALIZADO)
   */
  get totalPedidosActivos(): number {
    return this.pedidos.length;
  }

  // ✅ MÉTODOS PARA ACOMPAÑAMIENTOS MEJORADOS (MULTIFORMATO / MULTI-API)
  getAcompanamientosFormato(item: any): string {
    if (!item) return '';

    // 1. Evaluar si la API manda la propiedad formateada (venga con "ñ" o con "n")
    const formatoEspecial = item.acompañamientosFormato || item.acompanamientosFormato;
    if (formatoEspecial && typeof formatoEspecial === 'string') {
      const limpio = formatoEspecial.trim();
      if (limpio && !limpio.toLowerCase().includes('sin acompañamiento') && !limpio.toLowerCase().includes('sin acompanamiento')) {
        return limpio;
      }
    }

    // 2. Evaluar propiedad "acompanamientos" estándar
    if (item.acompanamientos && typeof item.acompanamientos === 'string') {
      const limpio = item.acompanamientos.trim();
      if (limpio && limpio !== 'Sin acompanamientos' && !limpio.toLowerCase().includes('sin acompañamiento')) {
        return limpio;
      }
    }

    // 3. Evaluar de forma dinámica si existen arrays "toppings" y "salsas"
    const partes: string[] = [];
    if (Array.isArray(item.toppings) && item.toppings.length > 0) {
      partes.push(`Toppings: ${item.toppings.join(', ')}`);
    }
    if (Array.isArray(item.salsas) && item.salsas.length > 0) {
      partes.push(`Salsas: ${item.salsas.join(', ')}`);
    }

    return partes.join(' | ');
  }

  contarAcompanamientos(item: any): number {
    const texto = this.getAcompanamientosFormato(item);
    if (!texto) return 0;
    const matches = texto.match(/,/g);
    return matches ? matches.length + 1 : 1;
  }

  tieneAcompanamientos(item: any): boolean {
    return this.getAcompanamientosFormato(item).length > 0;
  }

  getAcompanamientos(item: any): string {
    return this.getAcompanamientosFormato(item);
  }
}
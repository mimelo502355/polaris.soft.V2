import { Injectable } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PedidoService } from './pedido-service';
import { ClienteService } from './cliente.service';

@Injectable({
  providedIn: 'root'
})
export class PedidoMonitorService {

  private pedidoListoSubject = new BehaviorSubject<any>(null);
  public pedidoListo$ = this.pedidoListoSubject.asObservable();

  // ✅ Set de IDs de pedidos LISTO que ya hemos visto
  private pedidosListoVistos = new Set<string>();
  private inicializado = false;

  constructor(
    private pedidoService: PedidoService,
    private clienteService: ClienteService
  ) {
    this.iniciarMonitoring();
  }

  iniciarMonitoring(): void {
    interval(3000)
      .pipe(
        switchMap(() => this.pedidoService.getAll())
      )
      .subscribe({
        next: (response: any) => {
          const nombreCliente = this.clienteService.getNombres();
          
          if (!nombreCliente) return;

          const pedidosCliente = response.filter(
            (p: any) => p.nombreCliente === nombreCliente
          );

          // ✅ PRIMERA EJECUCIÓN: Marcar todos los LISTO actuales como vistos
          if (!this.inicializado) {
            this.inicializado = true;
            
            pedidosCliente
              .filter((p: any) => p.estado === 'LISTO')
              .forEach((p: any) => {
                this.pedidosListoVistos.add(p.idPedido);
              });
            
            return; // No emitir en la primera carga
          }

          // ✅ SIGUIENTES EJECUCIONES: Buscar NUEVOS pedidos en LISTO
          pedidosCliente.forEach((pedido: any) => {
            // ✅ SOLO procesar si está en LISTO
            if (pedido.estado === 'LISTO') {
              // ✅ Y es la primera vez que lo vemos en LISTO
              if (!this.pedidosListoVistos.has(pedido.idPedido)) {
                this.pedidosListoVistos.add(pedido.idPedido);
                this.pedidoListoSubject.next(pedido);
              }
            }
          });
        },
        error: (err) => {
          console.error('Error en monitoreo:', err);
        }
      });
  }

  /**
   * ✅ Limpiar cuando cliente cierra sesión
   */
  resetear(): void {
    this.inicializado = false;
    this.pedidosListoVistos.clear();
  }
}
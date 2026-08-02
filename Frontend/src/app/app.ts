import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Router } from '@angular/router';
import { delay } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { LoaderService } from './service/loader-service';
import { AuthInterceptor } from './service/auth.interceptor';
import { Loader } from './component/loader/loader';
import { CommonModule } from '@angular/common';
import { NotificationComponent } from './component/notification/notification.component';
import { NavbarClienteComponent } from './component/navbar-cliente/navbar-cliente.component';
import { CarritoFlotanteComponent } from './component/carrito-flotante/carrito-flotante.component';
import { HistorialFlotanteComponent } from './component/historial-flotante/historial-flotante.component';
import { ModalLoginDniComponent } from './component/modal-login-dni/modal-login-dni.component';
import { ModalClienteService } from './service/modal-cliente.service';
import { NotificationService } from './service/notification.service';
import { ModalPedidoListoComponent } from './component/modal-pedido-listo/modal-pedido-listo.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterModule,
    HttpClientModule,
    Loader,
    CommonModule,
    NotificationComponent,
    NavbarClienteComponent,
    CarritoFlotanteComponent,
    HistorialFlotanteComponent,
    ModalLoginDniComponent,
    ModalPedidoListoComponent
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  loading$: Observable<boolean>;
  modalClienteAbierto$: Observable<boolean>;

  constructor(
    private loaderService: LoaderService,
    private modalClienteService: ModalClienteService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.loading$ = this.loaderService.loading$.pipe(delay(0));
    this.modalClienteAbierto$ = this.modalClienteService.modalAbierto$;
  }

  protected readonly title = signal('POLARIS-LIMPIO');

  /**
   * ✅ Cerrar modal cliente
   */
  cerrarModalCliente(): void {
    this.modalClienteService.cerrarModal();
  }

  /**
 * ✅ Login cliente exitoso
 * NO navega automáticamente - deja que el usuario continúe donde está
 */
loginClienteExitoso(): void {
  this.modalClienteService.cerrarModal();
  this.notificationService.success('¡Sesión iniciada correctamente!');
  
  // ✅ NO navega - Se queda en la página actual para agregar productos
}}
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * ✅ Servicio para controlar modal de login DNI
 */
@Injectable({
  providedIn: 'root'
})
export class ModalClienteService {
  private modalAbiertoSubject = new BehaviorSubject<boolean>(false);
  public modalAbierto$: Observable<boolean> = this.modalAbiertoSubject.asObservable();

  /**
   * Abre el modal
   */
  abrirModal(): void {
    this.modalAbiertoSubject.next(true);
  }

  /**
   * Cierra el modal
   */
  cerrarModal(): void {
    this.modalAbiertoSubject.next(false);
  }

  /**
   * Verifica si modal está abierto
   */
  isAbierto(): boolean {
    return this.modalAbiertoSubject.value;
  }
}
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../service/cliente.service';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-modal-recuperar-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-recuperar-password.component.html',
  styleUrl: './modal-recuperar-password.component.css'
})
export class ModalRecuperarPasswordComponent {
  
  mostrarModal = false;
  
  // ✅ PASO 1: DNI + EMAIL
  paso = 1;
  dni = '';
  email = '';
  
  // ✅ PASO 2: CÓDIGO OTP + CONTRASEÑA
  codigo = '';
  passwordNueva = '';
  passwordConfirm = '';
  
  // ✅ ESTADOS
  enviando = false;
  cargando = false;
  mensajeExito = false;

  constructor(
    private clienteService: ClienteService,
    private notificationService: NotificationService
  ) {}

  /**
   * ✅ Abrir modal
   */
  abrir(): void {
    this.mostrarModal = true;
    this.resetForm();
    this.paso = 1;
  }

  /**
   * ✅ Cerrar modal
   */
  cerrar(): void {
    this.mostrarModal = false;
    this.resetForm();
  }

  /**
   * ✅ Resetear formulario
   */
  private resetForm(): void {
    this.dni = '';
    this.email = '';
    this.codigo = '';
    this.passwordNueva = '';
    this.passwordConfirm = '';
    this.enviando = false;
    this.cargando = false;
    this.mensajeExito = false;
    this.paso = 1;
  }

  /**
   * ✅ PASO 1: Enviar email con código OTP
   */
  enviarCodigoEmail(): void {
    if (!this.dni || this.dni.length !== 8) {
      this.notificationService.error('Ingresa un DNI válido (8 dígitos)');
      return;
    }

    if (!this.email || !this.email.includes('@')) {
      this.notificationService.error('Ingresa un email válido');
      return;
    }

    this.enviando = true;

    this.clienteService.solicitudRecuperacionPassword(this.dni, this.email).subscribe({
      next: (response: any) => {
        this.enviando = false;
        if (response.success) {
          this.paso = 2;
          this.notificationService.success('Código enviado a tu email 📧');
        }
      },
      error: (err) => {
        this.enviando = false;
        console.error('Error:', err);
        this.notificationService.error(err.error?.error || 'Error al enviar el código');
      }
    });
  }

  /**
   * ✅ PASO 2: Confirmar código y cambiar contraseña
   */
  confirmarRecuperacion(): void {
    if (!this.codigo || this.codigo.length !== 6) {
      this.notificationService.error('Ingresa un código válido (6 dígitos)');
      return;
    }

    if (!this.passwordNueva || this.passwordNueva.length < 6) {
      this.notificationService.error('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    if (this.passwordNueva !== this.passwordConfirm) {
      this.notificationService.error('Las contraseñas no coinciden');
      return;
    }

    this.cargando = true;

    this.clienteService.confirmarRecuperacionPassword(
      this.dni,
      this.email,
      this.codigo,
      this.passwordNueva,
      this.passwordConfirm
    ).subscribe({
      next: (response: any) => {
        this.cargando = false;
        if (response.success) {
          this.paso = 3;
          this.mensajeExito = true;
          this.notificationService.success('Contraseña actualizada ✅');

          setTimeout(() => {
            this.cerrar();
          }, 2000);
        }
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error:', err);
        this.notificationService.error(err.error?.error || 'Error al actualizar la contraseña');
      }
    });
  }

  /**
   * ✅ Volver al paso 1
   */
  volverAlPaso1(): void {
    this.paso = 1;
    this.codigo = '';
    this.passwordNueva = '';
    this.passwordConfirm = '';
  }
}
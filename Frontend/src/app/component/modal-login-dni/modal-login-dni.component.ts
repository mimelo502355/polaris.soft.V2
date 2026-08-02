import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClienteService } from '../../service/cliente.service';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-modal-login-dni',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './modal-login-dni.component.html',
  styleUrl: './modal-login-dni.component.css'
})
export class ModalLoginDniComponent {
  
  @Output() cerrar = new EventEmitter<void>();
  @Output() loginExitoso = new EventEmitter<void>();
  
  // ✅ TABS Y GENERAL
  activeTab: 'login' | 'registro' = 'login';
  loading = false;
  formLogin: FormGroup;
  
  // ✅ REGISTRO
  paso: 'dni' | 'email' | 'otp' | 'confirmacion' = 'dni';
  dniRegistro = '';
  emailRegistro = '';
  codigoOtp = '';
  passwordRegistro = '';
  passwordConfirm = '';
  datosReniec: any = null;
  
  // ✅ RECUPERACIÓN
  mostrarModalRecuperacion = false;
  dniRecuperacion = '';
  emailRecuperacion = '';
  codigoRecuperacion = '';
  passwordRecuperacion = '';
  passwordRecuperacionConfirm = '';
  pasoRecuperacion = 1;
  enviandoRecuperacion = false;
  enviandoConfirmacion = false;

  constructor(
    private clienteService: ClienteService,
    private notificationService: NotificationService,
    private fb: FormBuilder
  ) {
    this.formLogin = this.fb.group({
      dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  /**
   * ✅ LOGIN
   */
  login(): void {
    if (this.formLogin.invalid) {
      this.notificationService.warning('Completa DNI y contraseña correctamente');
      return;
    }
    this.loading = true;
    const { dni, password } = this.formLogin.value;
    
    this.clienteService.loginCliente(dni, password).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          sessionStorage.removeItem('auth_token');
          sessionStorage.removeItem('auth_rol');
          sessionStorage.removeItem('auth_idEmpleado');
          sessionStorage.removeItem('auth_nombre');
          
          const token = 'cliente_' + response.idCliente;
          sessionStorage.setItem('cliente_token', token);
          sessionStorage.setItem('cliente_idCliente', response.idCliente);
          sessionStorage.setItem('cliente_nombres', response.nombres);
          sessionStorage.setItem('cliente_dni', response.dni);
          
          this.notificationService.success(`¡Bienvenido, ${response.nombres}!`);
          setTimeout(() => {
            this.loginExitoso.emit();
            this.cerrarModal();
          }, 500);
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.notificationService.error(err.error?.error || 'Error en el login');
      }
    });
  }

  /**
   * ✅ REGISTRO PASO 1: Verificar DNI en RENIEC
   */
  verificarDni(): void {
    if (!this.dniRegistro || !this.dniRegistro.match(/^\d{8}$/)) {
      this.notificationService.warning('Ingresa un DNI válido (8 dígitos)');
      return;
    }
    
    this.loading = true;
    this.clienteService.registroDni(this.dniRegistro).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          this.datosReniec = response.datos;
          this.paso = 'email';
          this.notificationService.success('DNI verificado ✅');
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.notificationService.error(err.error?.error || 'DNI no encontrado en RENIEC');
      }
    });
  }

  /**
   * ✅ REGISTRO PASO 2: Enviar Email con OTP
   */
  enviarCodigoEmail(): void {
    if (!this.emailRegistro || !this.emailRegistro.includes('@')) {
      this.notificationService.warning('Ingresa un email válido');
      return;
    }
    
    this.loading = true;
    this.clienteService.enviarCodigoEmail(this.dniRegistro, this.emailRegistro).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          this.paso = 'otp';
          this.notificationService.success('Código enviado a tu email 📧');
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.notificationService.error(err.error?.error || 'Error al enviar el email');
      }
    });
  }

  /**
   * ✅ REGISTRO PASO 3: Confirmar OTP y crear cuenta
   */
  confirmarCodigo(): void {
    if (!this.codigoOtp || this.codigoOtp.length !== 6) {
      this.notificationService.warning('Ingresa un código válido de 6 dígitos');
      return;
    }
    
    if (this.passwordRegistro !== this.passwordConfirm) {
      this.notificationService.warning('Las contraseñas no coinciden');
      return;
    }
    
    if (this.passwordRegistro.length < 6) {
      this.notificationService.warning('La contraseña debe tener al menos 6 caracteres');
      return;
    }
    
    this.loading = true;
    
    const datosConfirmacion = {
      dni: this.dniRegistro,
      nombres: this.datosReniec.nombres,
      apellido_paterno: this.datosReniec.apellido_paterno,
      apellido_materno: this.datosReniec.apellido_materno || '',
      email: this.emailRegistro,
      password: this.passwordRegistro,
      codigo: this.codigoOtp
    };
    
    this.clienteService.confirmarOtp(datosConfirmacion).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          this.notificationService.success('Cuenta creada exitosamente ✅');
          this.paso = 'confirmacion';
          setTimeout(() => {
            this.loginExitoso.emit();
            this.cerrarModal();
          }, 1500);
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.notificationService.error(err.error?.error || 'Error al guardar la cuenta');
      }
    });
  }

  /**
   * ✅ RECUPERACIÓN PASO 1: Enviar email con OTP
   */
  enviarRecuperacion(): void {
    if (!this.dniRecuperacion || this.dniRecuperacion.length !== 8) {
      this.notificationService.error('Ingresa un DNI válido (8 dígitos)');
      return;
    }
    
    if (!this.emailRecuperacion || !this.emailRecuperacion.includes('@')) {
      this.notificationService.error('Ingresa un email válido');
      return;
    }
    
    this.enviandoRecuperacion = true;
    
    this.clienteService.solicitudRecuperacionPassword(this.dniRecuperacion, this.emailRecuperacion).subscribe({
      next: (response: any) => {
        this.enviandoRecuperacion = false;
        if (response.success) {
          this.pasoRecuperacion = 2;
          this.notificationService.success('Código enviado a tu email ✅');
        }
      },
      error: (err: any) => {
        this.enviandoRecuperacion = false;
        this.notificationService.error(err.error?.error || 'Error al enviar email');
      }
    });
  }

  /**
   * ✅ RECUPERACIÓN PASO 2: Confirmar OTP y cambiar contraseña
   */
  confirmarRecuperacion(): void {
    if (!this.codigoRecuperacion || this.codigoRecuperacion.length !== 6) {
      this.notificationService.error('Código inválido');
      return;
    }
    
    if (this.passwordRecuperacion !== this.passwordRecuperacionConfirm) {
      this.notificationService.error('Las contraseñas no coinciden');
      return;
    }
    
    if (this.passwordRecuperacion.length < 6) {
      this.notificationService.error('La contraseña debe tener al menos 6 caracteres');
      return;
    }
    
    this.enviandoConfirmacion = true;
    
    this.clienteService.confirmarRecuperacionPassword(
      this.dniRecuperacion,
      this.emailRecuperacion,
      this.codigoRecuperacion,
      this.passwordRecuperacion,
      this.passwordRecuperacionConfirm
    ).subscribe({
      next: (response: any) => {
        this.enviandoConfirmacion = false;
        if (response.success) {
          this.pasoRecuperacion = 3;
          this.notificationService.success('Contraseña actualizada ✅');
          setTimeout(() => {
            this.mostrarModalRecuperacion = false;
            this.pasoRecuperacion = 1;
            this.dniRecuperacion = '';
            this.emailRecuperacion = '';
            this.codigoRecuperacion = '';
            this.passwordRecuperacion = '';
            this.passwordRecuperacionConfirm = '';
          }, 2000);
        }
      },
      error: (err: any) => {
        this.enviandoConfirmacion = false;
        this.notificationService.error(err.error?.error || 'Error al actualizar contraseña');
      }
    });
  }

  cambiarTab(tab: 'login' | 'registro'): void {
    this.activeTab = tab;
    this.resetFormularios();
  }

  reiniciarRegistro(): void {
    this.paso = 'dni';
    this.dniRegistro = '';
    this.emailRegistro = '';
    this.codigoOtp = '';
    this.passwordRegistro = '';
    this.passwordConfirm = '';
    this.datosReniec = null;
  }

  resetFormularios(): void {
    this.formLogin.reset();
    this.reiniciarRegistro();
  }

  cerrarModal(): void {
    this.resetFormularios();
    this.cerrar.emit();
  }
}
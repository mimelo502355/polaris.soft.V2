import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginService } from '../../service/login-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  formUser: FormGroup;
  loading = false;

  get nombreFb() { return this.formUser.controls['nombre']; }
  get passwordFb() { return this.formUser.controls['password']; }

  constructor(
    private formBuilder: FormBuilder,
    private userService: LoginService,
    private router: Router,
    private notificationService: NotificationService
  ) {
    this.formUser = this.formBuilder.group({
      nombre: [null, [Validators.required, Validators.minLength(3)]],
      password: [null, [Validators.required, Validators.minLength(4)]],
    });
  }

  public login(): void {
    if (this.formUser.invalid) {
      this.notificationService.warning('Por favor completa todos los campos correctamente.');
      return;
    }

    this.loading = true;

    const payload = {
      nombre: this.nombreFb.value,
      password: this.passwordFb.value
    };

    this.userService.login(payload).subscribe({
      next: (response: any) => {
        this.loading = false;

        if (response.success && response.token) {
          this.userService.setToken(response.token);
          this.userService.setRol(response.rol);
          this.userService.setIdEmpleado(response.idEmpleado);
          this.userService.setNombre(response.nombre);

          this.notificationService.success(`¡Bienvenido, ${response.nombre}!`);

          setTimeout(() => {
            if (response.rol === 'EMPLEADO') {
              this.router.navigate(['/empleado']);
            } else if (response.rol === 'SUPERADMIN') {
              this.router.navigate(['/panel-superadmin']);
            }
          }, 500);
        } else {
          this.notificationService.error(response.mensaje || 'Error en el login');
        }
      },
      error: (err) => {
        this.loading = false;
        
        if (err.status === 401) {
          this.notificationService.error('Credenciales incorrectas. Verifica usuario y contraseña.');
        } else if (err.status === 0) {
          this.notificationService.error('Error de conexión. Verifica que el servidor esté activo.');
        } else {
          this.notificationService.error('Error de conexión con el servidor. Intenta más tarde.');
        }
      }
    });
  }
}
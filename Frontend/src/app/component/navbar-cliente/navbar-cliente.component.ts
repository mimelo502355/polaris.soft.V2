import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';

/**
 * ✅ Navbar Cliente - Barra superior
 * Muestra "Bienvenido: [nombre]" si cliente está logueado
 */
@Component({
  selector: 'app-navbar-cliente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar-cliente.component.html',
  styleUrl: './navbar-cliente.component.css'
})
export class NavbarClienteComponent implements OnInit, OnDestroy {

  nombreCliente: string | null = null;
  logueado = false;
  verificacionInterval: any;

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.verificarCliente();

    // ✅ Verificar cambios cada 500ms
    this.verificacionInterval = setInterval(() => {
      this.verificarCliente();
    }, 500);
  }

  ngOnDestroy(): void {
    if (this.verificacionInterval) {
      clearInterval(this.verificacionInterval);
    }
  }

  /**
   * Verificar si cliente está logueado
   */
  verificarCliente(): void {
    this.logueado = this.clienteService.isClienteLogueado();
    this.nombreCliente = this.clienteService.getNombres();
  }

  /**
   * ✅ Logout cliente
   */
  logout(): void {
    this.clienteService.logoutCliente();
    this.logueado = false;
    this.nombreCliente = null;
    this.router.navigate(['/categoria']);
  }
}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CarritoService } from '../../service/carrito-service';
import { ClienteService } from '../../service/cliente.service';
import { PedidoService } from '../../service/pedido-service';
import { DtoPedidoItem, PedidoItemService } from '../../service/pedido-item';
import { NotificationService } from '../../service/notification.service';

/**
 * ✅ Componente Pedido - SOLO para clientes logueados
 * Pre-llena nombre con datos del cliente
 * Guarda pedido con nombre correcto del cliente
 */
@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pedido.html',
  styleUrls: ['./pedido.css'],
})
export class Pedido implements OnInit {
  formPedido!: FormGroup;
  carrito: any[] = [];
  total = 0;
  nombreClienteActual: string = '';
  mesas = [
    { numero: 1, ocupada: false },
    { numero: 2, ocupada: false },
    { numero: 3, ocupada: false },
    { numero: 4, ocupada: false },
  ];

  get nombreClienteFb() { return this.formPedido.controls['nombreCliente']; }
  get tipoPedidoFb() { return this.formPedido.controls['tipoPedido']; }
  get metodoPagoFb() { return this.formPedido.controls['metodoPago']; }
  get mesaFb() { return this.formPedido.controls['mesa']; }
  get totalPagarFb() { return this.formPedido.controls['totalPagar']; }

  constructor(
    private fb: FormBuilder,
    private carritoService: CarritoService,
    private clienteService: ClienteService,
    private pedidoService: PedidoService,
    private pedidoItemService: PedidoItemService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.formPedido = this.fb.group({
      nombreCliente: ['', Validators.required],
      tipoPedido: ['', Validators.required],
      metodoPago: ['', Validators.required],
      mesa: [null],
      totalPagar: [this.total]
    });
  }

  ngOnInit(): void {
    // ✅ Verificar que cliente esté logueado
    if (!this.clienteService.isClienteLogueado()) {
      this.notificationService.error('Debes iniciar sesión para hacer un pedido');
      this.router.navigate(['/categoria']);
      return;
    }

    // ✅ Obtener nombre del cliente desde ClienteService
    this.nombreClienteActual = this.clienteService.getNombres() || '';

    // ✅ Cargar carrito
    this.carrito = this.carritoService.obtenerItems();
    this.total = this.carritoService.total();

    // ✅ Pre-llenar formulario con datos del cliente
    this.formPedido.patchValue({
      nombreCliente: this.nombreClienteActual,
      totalPagar: this.total
    });

    // ✅ El nombre del cliente no debe poder cambiar
    this.formPedido.get('nombreCliente')?.disable();
  }

  /**
   * ✅ Crear pedido - CON VALIDACIÓN DE CARRITO VACÍO
   */
  insert(): void {
    // ✅ VALIDAR QUE EL CARRITO NO ESTÉ VACÍO
    if (!this.carrito || this.carrito.length === 0) {
      this.notificationService.error('⚠️ Tu carrito está vacío. Agrega productos antes de confirmar.');
      return;
    }

    // ✅ VALIDAR QUE EL TOTAL NO SEA 0
    if (this.total === 0 || this.total === null || this.total === undefined) {
      this.notificationService.error('❌ No puedes confirmar un pedido sin productos (Total: S/. 0)');
      return;
    }

    // Validar tipo de pedido
    if (this.formPedido.value.tipoPedido === 'MESA' && !this.formPedido.value.mesa) {
      this.notificationService.warning('Selecciona una mesa');
      return;
    }

    // Validar que el formulario sea válido (excepto nombreCliente que está disabled)
    if (!this.formPedido.value.tipoPedido || !this.formPedido.value.metodoPago) {
      this.notificationService.warning('Completa todos los datos');
      return;
    }

    // ✅ Construir payload del pedido CON el nombre del cliente logueado
    const pedidoPayload = {
      nombreCliente: this.nombreClienteActual, // ✅ CRÍTICO: Usar nombre del cliente logueado
      tipoPedido: this.tipoPedidoFb.value,
      metodoPago: this.metodoPagoFb.value,
      mesa: this.tipoPedidoFb.value === 'MESA' && this.mesaFb.value ? Number(this.mesaFb.value) : null,
      totalPagar: Number(String(this.totalPagarFb.value).replace(/[$,\s]/g, '')),
      estado: 'ESPERA'
    };

    this.pedidoService.insert(pedidoPayload).subscribe({
      next: (res: any) => {
        const idPedido = res.idPedido;
        console.log('✅ Pedido creado con ID:', idPedido);

        // ✅ Limpiar localStorage para evitar conflictos
        localStorage.removeItem('idPedido');
        localStorage.removeItem('pedido');

        // ✅ Guardar solo en sessionStorage (sesión actual)
        sessionStorage.setItem('idPedido', idPedido);

        // ✅ Agregar items del carrito
        this.carrito.forEach((item, index) => {
          console.log(`📦 Insertando item ${index + 1}/${this.carrito.length}:`, item);
          this.insertPedidoItem(idPedido, item);
        });

        // ✅ Limpiar carrito
        this.carritoService.limpiar();
        this.formPedido.reset();
        this.total = 0;

        this.notificationService.success('¡Pedido realizado exitosamente! ✅');

        // ✅ Navegar a estado-pedido
        setTimeout(() => {
          this.router.navigate(['/estado-pedido']);
        }, 1000);
      },
      error: (err) => {
        console.error('Error al insertar pedido', err);
        this.notificationService.error('Error al enviar el pedido');
      }
    });
  }

  /**
   * ✅ Insertar items del pedido CON TOPPINGS Y SALSAS - VERSIÓN MEJORADA
   */
  insertPedidoItem(idPedido: string, item: any): void {
    console.log('\n🛒 ============== INSERTANDO ITEM ==============');
    console.log('Item completo:', item);
    console.log('  → idProducto:', item.idProducto);
    console.log('  → cantidad:', item.cantidad);
    console.log('  → precioBase:', item.precioBase);
    console.log('  → toppings:', item.toppings);
    console.log('  → salsas:', item.salsas);

    // ✅ CONSTRUIR STRINGS DE ACOMPAÑAMIENTOS
    const partesAcomp: string[] = [];

    // Si existen toppings
    if (item.toppings && Array.isArray(item.toppings) && item.toppings.length > 0) {
      const toppingNames = item.toppings.map((t: any) => {
        // Si es string, usa directamente
        if (typeof t === 'string') {
          return t;
        }
        // Si es objeto, extrae el nombre
        return t.nombre || t.name || String(t);
      }).join(', ');
      partesAcomp.push(`Toppings: ${toppingNames}`);
      console.log('✅ Toppings:', toppingNames);
    }

    // Si existen salsas
    if (item.salsas && Array.isArray(item.salsas) && item.salsas.length > 0) {
      const salsaNames = item.salsas.map((s: any) => {
        // Si es string, usa directamente
        if (typeof s === 'string') {
          return s;
        }
        // Si es objeto, extrae el nombre
        return s.nombre || s.name || String(s);
      }).join(', ');
      partesAcomp.push(`Salsas: ${salsaNames}`);
      console.log('✅ Salsas:', salsaNames);
    }

    const acompanamientos = partesAcomp.length > 0 ? partesAcomp.join(' | ') : '';
    console.log('📝 Acompañamientos finales:', acompanamientos);

    // ✅ CONSTRUIR DTO JSON (NO FormData)
    const dtoPedidoItem: DtoPedidoItem = {
      idItem: '',
      idProducto: item.idProducto,
      idPedido: idPedido,
      cantidad: item.cantidad,
      precioUnitarioFinal: item.precioBase,
      acompanamientos: acompanamientos,
      toppings: item.toppings || [],
      salsas: item.salsas || [],
      toppingsIds: item.toppingsIds || [],
      salsasIds: item.salsasIds || [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    console.log('📤 DTO a enviar:');
    console.log('  → idProducto:', dtoPedidoItem.idProducto);
    console.log('  → cantidad:', dtoPedidoItem.cantidad);
    console.log('  → precioUnitarioFinal:', dtoPedidoItem.precioUnitarioFinal);
    console.log('  → acompanamientos:', dtoPedidoItem.acompanamientos);
    console.log('================================================\n');

    // ✅ ENVIAR COMO JSON
    this.pedidoItemService.insert(idPedido, dtoPedidoItem).subscribe({
      next: (res) => {
        console.log('✅ Item insertado correctamente:', item.idProducto);
        console.log('   Respuesta del servidor:', res);
      },
      error: (err: any) => {
        console.error('❌ Error al agregar item:', err);
        this.notificationService.error('Error al agregar item al pedido');
      }
    });
  }
}
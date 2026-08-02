import { Component, OnInit, OnDestroy, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ReporteService } from '../../service/reporte-service';
import { PedidoService } from '../../service/pedido-service';
import { EmpleadoService } from '../../service/empleado-service';

// Chart.js
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

// Librería para exportar Excel
import * as XLSX from 'xlsx';

// --- INTERFACES PARA TIPADO SEGURO ---
export interface Empleado {
  idEmpleado?: string;
  id?: string;
  nombre: string;
  password?: string;
  rol?: string;
  estado?: boolean;
  activo?: boolean;
  [key: string]: any;
}

export interface Categoria {
  idCategoria?: string;
  id?: string;
  nombre: string;
}

export interface Producto {
  idProducto: string;
  idCategoria: string;
  nombre: string;
  descripcion?: string;
  precioBase: number;
  disponible: boolean;
  imagenUrl?: string;
}

export interface ItemVenta {
  idItem?: string;
  nombreProducto?: string;
  nombre?: string;
  producto?: { nombre: string };
  cantidad: number;
  precioUnitarioFinal?: number;
}

export interface Venta {
  idPedido?: string;
  id?: string;
  totalPagar: number;
  estado?: string;
  metodoPago?: string;
  tipoPedido?: string;
  mesa?: number;
  createdAt?: any;
  fechaHora?: any;
  items?: ItemVenta[];
  [key: string]: any;
}

@Component({
  selector: 'app-panel-superadmin',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './panel-superadmin.html',
  styleUrls: ['./panel-superadmin.css']
})
export class PanelSuperadminComponent implements OnInit, OnDestroy {

  readonly API_URL = 'http://localhost:8081';
  private destroyRef = inject(DestroyRef);

  section: 'dashboard' | 'empleados' | 'productos' | 'ventas' = 'dashboard';

  // Métricas
  totalHoy: number = 0;
  totalEmpleados: number = 0;
  empleadosActivos: number = 0;
  totalProductos: number = 0;
  totalCategorias: number = 0;
  totalVentasSemana: number = 0;
  topProductoNombre: string = 'Sin datos';

  filtroActivo: 'hoy' | 'semana' | 'mes' | 'custom' = 'semana';

  // Listas de datos
  empleados: Empleado[] = [];
  categorias: Categoria[] = [];
  productosAgrupadosPorCategoria: { categoria: string; items: Producto[] }[] = [];
  ventas: Venta[] = [];
  ventasFiltradasActuales: Venta[] = [];

  // Estados de edición
  modoEdicionEmpleado: boolean = false;
  empleadoEditandoId: string | null = null;

  modoEdicionProducto: boolean = false;
  productoEditandoId: string | null = null;
  archivoImagenSeleccionado: File | null = null;

  // Gráficos
  private chartLinea: Chart | null = null;
  private chartMetodosPie: Chart | null = null;
  private chartProductosPie: Chart | null = null;

  formEmpleado!: FormGroup;
  formProducto!: FormGroup;
  formFiltro!: FormGroup;

  private temporalProductosCache: Producto[] = [];

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private empleadoService: EmpleadoService,
    private reporteService: ReporteService,
    private pedidoService: PedidoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.cargarTodoElSistema();
    this.filtrarSemana();
  }

  ngOnDestroy(): void {
    this.destruirGraficos();
  }

  // 🔹 MÉTODOS TRACKBY PARA OPTIMIZACIÓN DE RENDERIZADO (*ngFor)
  trackByEmpleadoId(index: number, item: Empleado): string | number {
    return item.idEmpleado || item.id || index;
  }

  trackByProductoId(index: number, item: Producto): string | number {
    return item.idProducto || index;
  }

  trackByVentaId(index: number, item: Venta): string | number {
    return item.idPedido || item.id || index;
  }

  // 🔹 MÉTODOS PARA MANEJO DE IMÁGENES (CORREGIDO)
  // 🔹 MÉTODOS PARA MANEJO DE IMÁGENES (UNIFICADO: BD + MAPA ESTÁTICO)
  // 🔹 MÉTODOS PARA MANEJO DE IMÁGENES (INTEGRADO CON WEBCONFIG SPRING BOOT)
  getImagenProducto(p: any): string {
    if (!p) return '/logo.png';

    // 1. Obtener la cadena de la imagen
    const imagenUrl: string = typeof p === 'object' ? p.imagenUrl : (typeof p === 'string' ? p : null);

    if (imagenUrl && typeof imagenUrl === 'string' && imagenUrl.trim() !== '') {
      const urlLimpia = imagenUrl.trim();

      // A) Si es URL completa externa (http/https)
      if (urlLimpia.startsWith('http://') || urlLimpia.startsWith('https://')) {
        return urlLimpia;
      }

      // B) Si la imagen es subida dinámicamente (contiene timestamp '_17' o carpetas de carga)
      // Apuntamos al Backend (localhost:8081) usando tu WebConfig
      if (urlLimpia.includes('_17') || urlLimpia.includes('waffles/') || urlLimpia.includes('jugos-ensaladas-y-sandwiches/')) {
        const rutaFormateada = urlLimpia.startsWith('/') ? urlLimpia : '/' + urlLimpia;
        return `${this.API_URL}${rutaFormateada}`; // Devuelve: http://localhost:8081/img/...
      }

      // C) Si es una imagen estática estándar
      return urlLimpia.startsWith('/') ? urlLimpia : '/' + urlLimpia;
    }

    // 2. FALLBACK POR ID (Para productos antiguos sin imagenUrl)
    const idProducto: string = typeof p === 'object' ? p.idProducto : p;
    if (!idProducto) return '/logo.png';

    const idLimpio = idProducto.trim().toLowerCase();

    const imagenesEstaticas: { [key: string]: string } = {
      'p0000001-0000-0000-0000-000000000001': 'img/jugos/jugo-fresa-con-leche.png',
      'p0000002-0000-0000-0000-000000000002': 'img/jugos/jugo-mango-con-leche.png',
      'p0000003-0000-0000-0000-000000000003': 'img/jugos/jugo-platano-con-leche.png',
      'p0000004-0000-0000-0000-000000000004': 'img/jugos/jugo-papaya-con-leche.png',
      'p0000005-0000-0000-0000-000000000005': 'img/jugos/jugo-surtido.png',
      'p0000006-0000-0000-0000-000000000006': 'img/jugos/jugo-especial.png',
      'p0000007-0000-0000-0000-000000000007': 'img/jugos/pina-colada.png',
      'p0000008-0000-0000-0000-000000000008': 'img/jugos/limonada-frozen-1l.png',
      'p0000009-0000-0000-0000-000000000009': 'img/jugos/infusiones.png',
      'p0000010-0000-0000-0000-000000000010': 'img/jugos/cafe-expresso.png',
      'p0000011-0000-0000-0000-000000000011': 'img/ensalada/ensalada-frutimix.png',
      'p0000012-0000-0000-0000-000000000012': 'img/ensalada/ensalada-gourmet.png',
      'p0000020-0000-0000-0000-000000000020': 'img/cafe/cafe-americano.png',
      'p0000021-0000-0000-0000-000000000021': 'img/cafe/cafe-expresso.png',
      'p0000022-0000-0000-0000-000000000022': 'img/cafe/cafe-capuchino.png',
      'p0000023-0000-0000-0000-000000000023': 'img/cafe/cafe-afogato.png',
      'p0000024-0000-0000-0000-000000000024': 'img/cafe/cafe-mocaccino.png',
      'p0000025-0000-0000-0000-000000000025': 'img/cafe/cafe-macchiato.png',
      'p0000026-0000-0000-0000-000000000026': 'img/cafe/cafe-bombon.png',
      'p0000030-0000-0000-0000-000000000030': 'img/frappe/frappuccino.png',
      'p0000031-0000-0000-0000-000000000031': 'img/frappe/frappe-vainilla.png',
      'p0000032-0000-0000-0000-000000000032': 'img/frappe/frappe-caramelo.png',
      'p0000033-0000-0000-0000-000000000033': 'img/frappe/frappe-moca.png',
      'p0000034-0000-0000-0000-000000000034': 'img/frappe/frappe-oreo.png',
      'p0000035-0000-0000-0000-000000000035': 'img/frappe/frappe-fresa.png',
      'p0000036-0000-0000-0000-000000000036': 'img/frappe/frappe-arandano.png',
      'p0000037-0000-0000-0000-000000000037': 'img/frappe/frappe-maracuya.png',
      'p0000038-0000-0000-0000-000000000038': 'img/frappe/frappe-durazno.png',
      'p0000039-0000-0000-0000-000000000039': 'img/frappe/frappe-mango.png',
      'p0000040-0000-0000-0000-000000000040': 'img/frappe/frappe-taro.png',
      'p0000041-0000-0000-0000-000000000041': 'img/frappe/frappe-matcha.png',
      'p0000050-0000-0000-0000-000000000050': 'img/waffles/waffle.png',
      'p0000051-0000-0000-0000-000000000051': 'img/waffles/waffle-sencillo.png',
      'p0000052-0000-0000-0000-000000000052': 'img/waffles/waffle-especial.png',
      'p0000053-0000-0000-0000-000000000053': 'img/waffles/waffle-supremo.png',
      'p0000013-0000-0000-0000-000000000013': 'img/sandwich/sandwich-clasico.png',
      'p0000014-0000-0000-0000-000000000014': 'img/sandwich/sandwich-especial.png',
    };

    const rutaEstatica = imagenesEstaticas[idLimpio];
    return rutaEstatica ? (rutaEstatica.startsWith('/') ? rutaEstatica : '/' + rutaEstatica) : '/logo.png';
  } 

  manejarErrorImagen(event: Event): void {
    const target = event.target as HTMLImageElement;
    if (target) {
      target.src = '/logo.png';
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.archivoImagenSeleccionado = input.files[0];
    }
  }

  private obtenerFechaLocalISO(d: Date = new Date()): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  }

  private initForms(): void {
    this.formEmpleado = this.fb.group({
      idEmpleado: [''],
      nombre: ['', Validators.required],
      password: ['', Validators.required],
      rol: ['EMPLEADO', Validators.required]
    });

    this.formProducto = this.fb.group({
      idProducto: [''],
      idCategoria: ['', Validators.required],
      nombre: ['', Validators.required],
      descripcion: [''],
      precioBase: [null, [Validators.required, Validators.min(0.01)]],
      disponible: [true]
    });

    const hoy = this.obtenerFechaLocalISO();
    this.formFiltro = this.fb.group({
      inicio: [hoy],
      fin: [hoy]
    });
  }

  selectSection(sec: 'dashboard' | 'empleados' | 'productos' | 'ventas'): void {
    this.section = sec;
    if (sec === 'ventas' || sec === 'dashboard') {
      setTimeout(() => this.procesarMetricasVentas(), 150);
    }
  }

  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  private obtenerFechaObjeto(v: any): Date | null {
    if (!v || typeof v !== 'object') return null;

    const posiblesClaves = [
      'createdAt', 'created_at', 'fechaHora', 'fecha_hora',
      'fechahora', 'fecha', 'fechaCreacion', 'fecha_creacion',
      'fechaPedido', 'fecha_pedido', 'createdDate',
      'fechaRegistro', 'fecha_registro', 'timestamp', 'date', 'time'
    ];

    let raw: any = null;

    for (const clave of posiblesClaves) {
      if (v[clave] !== undefined && v[clave] !== null) {
        raw = v[clave];
        break;
      }
    }

    if (raw === null || raw === undefined) {
      for (const val of Object.values(v)) {
        if (Array.isArray(val) && val.length >= 3 && typeof val[0] === 'number' && val[0] > 2000) {
          raw = val;
          break;
        }
        if (typeof val === 'string' && /^\d{4}-\d{2}-\d{2}/.test(val)) {
          raw = val;
          break;
        }
      }
    }

    if (!raw) return null;

    if (Array.isArray(raw)) {
      if (raw.length < 3) return null;
      return new Date(
        raw[0],
        raw[1] - 1,
        raw[2],
        raw[3] || 0,
        raw[4] || 0,
        raw[5] || 0
      );
    }

    const d = new Date(raw);
    return isNaN(d.getTime()) ? null : d;
  }

  formatearFecha(v: any): string {
    const d = this.obtenerFechaObjeto(v);
    if (!d) return 'N/A';

    const pad = (n: number) => n.toString().padStart(2, '0');
    const dia = pad(d.getDate());
    const mes = pad(d.getMonth() + 1);
    const anio = d.getFullYear();
    const horas = pad(d.getHours());
    const minutos = pad(d.getMinutes());

    return `${dia}/${mes}/${anio} ${horas}:${minutos}`;
  }

  // --- CARGA DE DATOS ---

  cargarTodoElSistema(): void {
    this.cargarEmpleados();
    this.cargarCategorias();
    this.cargarProductos();
    this.cargarVentas();
  }

  cargarEmpleados(): void {
    this.http.get<any>(`${this.API_URL}/empleado`)
      .pipe(
        catchError(() => this.http.get<any>(`${this.API_URL}/empleado/getall`).pipe(catchError(() => of([])))),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (res: any) => {
          let rawData: any[] = Array.isArray(res) ? res : (res?.content || res?.data || []);
          const mapaUnicos = new Map<string, Empleado>();

          rawData.forEach((emp: any, index: number) => {
            const id = String(emp.idEmpleado || emp.id || emp.id_empleado || `emp-${index}`);
            const nombre = emp.nombre || emp.nombreEmpleado || emp.nombreCompleto || 'Empleado sin nombre';
            const rol = emp.rol || 'EMPLEADO';
            const estadoBool = emp.estado !== undefined 
              ? Boolean(emp.estado) 
              : (emp.activo !== undefined ? Boolean(emp.activo) : true);

            mapaUnicos.set(id, {
              ...emp,
              idEmpleado: id,
              nombre: nombre,
              rol: rol,
              estado: estadoBool,
              activo: estadoBool
            });
          });

          this.empleados = Array.from(mapaUnicos.values());
          this.totalEmpleados = this.empleados.length;
          this.empleadosActivos = this.empleados.filter(e => e.estado !== false && e.activo !== false).length;
        }
      });
  }

  cargarCategorias(): void {
    this.http.get<Categoria[]>(`${this.API_URL}/categoria`)
      .pipe(
        catchError((err) => {
          console.error('Error al cargar categorías:', err);
          return of([]);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((res) => {
        this.categorias = Array.isArray(res) ? res : (res as any)?.content || [];
        this.totalCategorias = this.categorias.length;
        this.agruparProductos();
      });
  }

  cargarProductos(): void {
    this.http.get<any>(`${this.API_URL}/producto/getall`)
      .pipe(
        catchError(() => this.http.get<any>(`${this.API_URL}/producto`).pipe(catchError(() => of([])))),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((res) => {
        const prods: Producto[] = Array.isArray(res) ? res : (res?.content || res?.data || []);
        this.totalProductos = prods.length;
        this.agruparProductos(prods);
      });
  }

  private agruparProductos(nuevosProductos?: Producto[]): void {
    if (nuevosProductos) {
      this.temporalProductosCache = nuevosProductos;
    }

    const prods = this.temporalProductosCache;

    if (prods.length === 0) {
      this.productosAgrupadosPorCategoria = [];
      return;
    }

    if (this.categorias.length === 0) {
      this.productosAgrupadosPorCategoria = [{
        categoria: 'General',
        items: prods
      }];
      return;
    }

    this.productosAgrupadosPorCategoria = this.categorias.map(cat => {
      const catId = cat.idCategoria || cat.id;
      const itemsCat = prods.filter(p => 
        p.idCategoria === catId || 
        (p as any).categoria?.idCategoria === catId || 
        (p as any).categoria?.id === catId
      );
      return {
        categoria: cat.nombre || 'Categoría',
        items: itemsCat
      };
    });

    const productosSinCat = prods.filter(p => 
      !this.categorias.some(c => (c.idCategoria || c.id) === (p.idCategoria || (p as any).categoria?.id))
    );

    if (productosSinCat.length > 0) {
      this.productosAgrupadosPorCategoria.push({
        categoria: 'Otros Productos',
        items: productosSinCat
      });
    }
  }

  cargarVentas(): void {
    this.pedidoService.getAll()
      .pipe(
        catchError(() => of([])),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (res: any) => {
          this.ventas = Array.isArray(res) ? res : (res?.content || res?.data || []);
          this.procesarMetricasVentas();
        }
      });
  }

  // --- FILTROS DE FECHAS Y MÉTRICAS ---

  procesarMetricasVentas(): void {
    const hoyStr = this.obtenerFechaLocalISO();
    const inicioVal = this.formFiltro.get('inicio')?.value || hoyStr;
    const finVal = this.formFiltro.get('fin')?.value || hoyStr;

    const inicioTime = new Date(`${inicioVal}T00:00:00`).getTime();
    const finTime = new Date(`${finVal}T23:59:59`).getTime();

    const hoyInicioTime = new Date(`${hoyStr}T00:00:00`).getTime();
    const hoyFinTime = new Date(`${hoyStr}T23:59:59`).getTime();

    let sumaHoy = 0;
    let sumaRango = 0;
    const productosConteo: { [nombre: string]: number } = {};

    this.ventasFiltradasActuales = [];

    this.ventas.forEach((v) => {
      const monto = Number(v.totalPagar) || 0;
      const fechaObj = this.obtenerFechaObjeto(v);
      const fechaPedidoTime = fechaObj ? fechaObj.getTime() : null;

      const estadoNormalizado = (v.estado || '').toString().toUpperCase();
      const esCancelado = estadoNormalizado === 'CANCELADO';

      if (fechaPedidoTime && fechaPedidoTime >= hoyInicioTime && fechaPedidoTime <= hoyFinTime) {
        if (!esCancelado) {
          sumaHoy += monto;
        }
      }

      const estaEnRango = fechaPedidoTime
        ? (fechaPedidoTime >= inicioTime && fechaPedidoTime <= finTime)
        : true;

      if (estaEnRango) {
        this.ventasFiltradasActuales.push(v);

        if (!esCancelado) {
          sumaRango += monto;

          if (v.items && Array.isArray(v.items)) {
            v.items.forEach((item: ItemVenta) => {
              const nombreProd = item.nombreProducto || item.nombre || item.producto?.nombre || 'Producto';
              const cantidad = item.cantidad || 1;
              productosConteo[nombreProd] = (productosConteo[nombreProd] || 0) + cantidad;
            });
          }
        }
      }
    });

    this.totalHoy = sumaHoy;
    this.totalVentasSemana = sumaRango;

    let maxCant = 0;
    let mejorProd = 'Sin datos';
    Object.entries(productosConteo).forEach(([nombre, cant]) => {
      if (cant > maxCant) {
        maxCant = cant;
        mejorProd = nombre;
      }
    });
    this.topProductoNombre = mejorProd !== 'Sin datos' ? `${mejorProd} (${maxCant} ud.)` : 'Sin datos';

    if (this.section === 'ventas') {
      setTimeout(() => this.actualizarGraficos(productosConteo), 100);
    }
  }

  filtrarHoy(): void {
    this.filtroActivo = 'hoy';
    const hoy = this.obtenerFechaLocalISO();
    this.formFiltro.patchValue({ inicio: hoy, fin: hoy });
    this.procesarMetricasVentas();
  }

  filtrarSemana(): void {
    this.filtroActivo = 'semana';
    const hoyDate = new Date();
    const hace7Dias = new Date();
    hace7Dias.setDate(hoyDate.getDate() - 6);

    this.formFiltro.patchValue({
      inicio: this.obtenerFechaLocalISO(hace7Dias),
      fin: this.obtenerFechaLocalISO(hoyDate)
    });
    this.procesarMetricasVentas();
  }

  filtrarMes(): void {
    this.filtroActivo = 'mes';
    const hoyDate = new Date();
    const inicioMes = new Date(hoyDate.getFullYear(), hoyDate.getMonth(), 1);

    this.formFiltro.patchValue({
      inicio: this.obtenerFechaLocalISO(inicioMes),
      fin: this.obtenerFechaLocalISO(hoyDate)
    });
    this.procesarMetricasVentas();
  }

  aplicarFiltroCustom(): void {
    this.filtroActivo = 'custom';
    this.procesarMetricasVentas();
  }

  // --- GESTIÓN DE EMPLEADOS ---

  guardarEmpleado(): void {
    if (this.formEmpleado.invalid) {
      alert('Por favor llena los campos obligatorios del empleado.');
      return;
    }

    const body: Empleado = { ...this.formEmpleado.value };

    if (this.modoEdicionEmpleado && this.empleadoEditandoId) {
      if (!body.password) {
        delete body.password;
      }

      this.http.put(`${this.API_URL}/empleado/update`, body)
        .pipe(
          catchError(() => this.http.put(`${this.API_URL}/empleado/${this.empleadoEditandoId}`, body)),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe({
          next: () => {
            alert('Empleado actualizado con éxito');
            this.cancelarEdicionEmpleado();
            this.cargarEmpleados();
          },
          error: (err) => alert('Error al actualizar empleado: ' + (err.error?.message || err.message))
        });
    } else {
      this.http.post(`${this.API_URL}/empleado/insert`, body)
        .pipe(
          catchError(() => this.http.post(`${this.API_URL}/empleado`, body)),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe({
          next: () => {
            alert('Empleado creado con éxito');
            this.cancelarEdicionEmpleado();
            this.cargarEmpleados();
          },
          error: (err) => alert('Error al crear empleado: ' + (err.error?.message || err.message))
        });
    }
  }

  eliminarEmpleado(empleado: any): void {
    const nombreLower = (empleado.nombre || '').toLowerCase().trim();

    if (nombreLower === 'admin' || empleado.rol === 'SUPERADMIN') {
      alert('🚫 Acción denegada: Es imposible eliminar al SuperAdmin del sistema.');
      return;
    }

    const confirmacion = confirm(
      `⚠️ ¿Estás seguro de ELIMINAR PERMANENTEMENTE a "${empleado.nombre}"?\nEsta acción lo borrará por completo de la base de datos.`
    );

    if (confirmacion) {
      const id = empleado.idEmpleado || empleado.id;

      this.http.delete(`${this.API_URL}/empleado/${id}`).subscribe({
        next: () => {
          alert('✅ Empleado eliminado permanentemente.');
          this.cargarEmpleados();
        },
        error: (err) => {
          console.error('Error al eliminar empleado:', err);
          const mensajeError = err.error?.error || 'Ocurrió un error al intentar eliminar el empleado.';
          alert(`❌ ${mensajeError}`);
        }
      });
    }
  }

  prepararEditarEmpleado(emp: Empleado): void {
    this.modoEdicionEmpleado = true;
    this.empleadoEditandoId = emp.idEmpleado || emp.id || null;

    const passControl = this.formEmpleado.get('password');
    if (passControl) {
      passControl.clearValidators();
      passControl.updateValueAndValidity();
    }

    this.formEmpleado.patchValue({
      idEmpleado: emp.idEmpleado || emp.id,
      nombre: emp.nombre,
      rol: emp.rol || 'EMPLEADO',
      password: ''
    });
  }

  cancelarEdicionEmpleado(): void {
    this.modoEdicionEmpleado = false;
    this.empleadoEditandoId = null;
    this.formEmpleado.reset({ rol: 'EMPLEADO' });

    const passControl = this.formEmpleado.get('password');
    if (passControl) {
      passControl.setValidators([Validators.required]);
      passControl.updateValueAndValidity();
    }
  }

  desactivarEmpleado(emp: Empleado): void {
    const id = emp.idEmpleado || emp.id;
    if (!id) return;

    const estaActivo = emp.estado !== false && emp.activo !== false;
    const nuevoEstado = !estaActivo;
    const accionText = nuevoEstado ? 'activar' : 'desactivar';

    if (!confirm(`¿Está seguro de ${accionText} a ${emp.nombre}?`)) return;

    const payloadActualizado = {
      ...emp,
      estado: nuevoEstado,
      activo: nuevoEstado
    };

    this.http.put(`${this.API_URL}/empleado/update`, payloadActualizado)
      .pipe(
        catchError(() => this.http.put(`${this.API_URL}/empleado/${id}`, payloadActualizado)),
        catchError(() => this.http.put(`${this.API_URL}/empleado/estado/${id}?estado=${nuevoEstado}`, {})),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          alert(`Empleado ${nuevoEstado ? 'activado' : 'desactivado'} con éxito`);
          this.cargarEmpleados();
        },
        error: (err) => alert('Error al cambiar el estado del empleado: ' + (err.error?.message || err.message))
      });
  }

  // --- GESTIÓN DE PRODUCTOS ---

  guardarProducto(): void {
    if (this.formProducto.invalid) {
      alert('Por favor completa todos los campos.');
      return;
    }

    const val = this.formProducto.value;
    const esEdicion = this.modoEdicionProducto && !!this.productoEditandoId;

    const dtoProducto = {
      idProducto: esEdicion ? this.productoEditandoId : (val.idProducto || null),
      idCategoria: val.idCategoria || '',
      nombre: val.nombre || '',
      descripcion: val.descripcion || '',
      precioBase: Number(val.precioBase) || 0,
      disponible: val.disponible ?? true
    };

    const formData = new FormData();
    const productoBlob = new Blob([JSON.stringify(dtoProducto)], { type: 'application/json' });
    formData.append('producto', productoBlob);

    if (this.archivoImagenSeleccionado) {
      formData.append('imagen', this.archivoImagenSeleccionado);
    }

    this.http.post(`${this.API_URL}/producto/update-con-imagen`, formData)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          alert(esEdicion ? '¡Producto actualizado correctamente!' : '¡Producto creado correctamente!');
          this.cancelarEdicionProducto();
          this.cargarProductos();
        },
        error: (err) => {
          console.error('Error devuelto por el backend:', err);
          alert('Error al guardar el producto: ' + (err.error?.message || err.error || err.message));
        }
      });
  }

  prepararEditarProducto(prod: Producto): void {
    this.modoEdicionProducto = true;
    this.productoEditandoId = prod.idProducto;
    this.archivoImagenSeleccionado = null;

    this.formProducto.patchValue({
      idProducto: prod.idProducto,
      idCategoria: prod.idCategoria,
      nombre: prod.nombre,
      descripcion: prod.descripcion || '',
      precioBase: prod.precioBase,
      disponible: prod.disponible
    });
  }

  cancelarEdicionProducto(): void {
    this.modoEdicionProducto = false;
    this.productoEditandoId = null;
    this.archivoImagenSeleccionado = null;
    this.formProducto.reset({ disponible: true, idCategoria: '' });

    const inputImg = document.getElementById('inputImagenProducto') as HTMLInputElement;
    if (inputImg) inputImg.value = '';
  }

  eliminarProducto(idProducto: string): void {
    if (!confirm('¿Seguro que deseas eliminar este producto y su imagen adjunta?')) return;

    this.http.delete(`${this.API_URL}/producto/${idProducto}`)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          alert('Producto e imagen eliminados correctamente');
          this.cargarProductos();
        },
        error: (err) => alert('Error al eliminar producto: ' + (err.error?.message || err.message))
      });
  }

  // --- GRÁFICOS CHART.JS ---

  actualizarGraficos(productosConteo: { [nombre: string]: number } = {}): void {
    if (this.section !== 'ventas') return;

    const { labels: labelsLinea, data: dataLinea } = this.generarDatosGraficoLinea();

    const metodosMap: { [key: string]: number } = {};
    const listaAnalisis = this.ventasFiltradasActuales;

    listaAnalisis.forEach((p) => {
      if ((p.estado || '').toString().toUpperCase() !== 'CANCELADO') {
        const metodo = (p.metodoPago || 'EFECTIVO').toString().toUpperCase();
        metodosMap[metodo] = (metodosMap[metodo] || 0) + (Number(p.totalPagar) || 0);
      }
    });

    const labelsMetodos = Object.keys(metodosMap).length > 0 ? Object.keys(metodosMap) : ['Sin ventas'];
    const dataMetodos = Object.values(metodosMap).length > 0 ? Object.values(metodosMap) : [0];

    const labelsProds = Object.keys(productosConteo).length > 0 ? Object.keys(productosConteo) : ['Sin ventas'];
    const dataProds = Object.values(productosConteo).length > 0 ? Object.values(productosConteo) : [0];

    this.crearGraficoLinea(labelsLinea, dataLinea);
    this.crearGraficoMetodosPie(labelsMetodos, dataMetodos);
    this.crearGraficoProductosPie(labelsProds, dataProds);
  }

  private generarDatosGraficoLinea(): { labels: string[]; data: number[] } {
    const lista = this.ventasFiltradasActuales;
    const hoyStr = this.obtenerFechaLocalISO();
    const inicioVal = this.formFiltro.get('inicio')?.value || hoyStr;
    const finVal = this.formFiltro.get('fin')?.value || hoyStr;

    const dInicio = new Date(`${inicioVal}T00:00:00`);
    const dFin = new Date(`${finVal}T23:59:59`);

    const diffMs = dFin.getTime() - dInicio.getTime();
    const diffDias = Math.max(1, Math.round(diffMs / (1000 * 60 * 60 * 24)));

    const mapValores = new Map<string, number>();
    const labels: string[] = [];

    if (diffDias <= 1) {
      const horas = ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'];
      horas.forEach(h => {
        mapValores.set(h, 0);
        labels.push(h);
      });

      lista.forEach(v => {
        if ((v.estado || '').toString().toUpperCase() === 'CANCELADO') return;
        const fecha = this.obtenerFechaObjeto(v);
        if (!fecha) return;

        const hora = fecha.getHours();
        let clave = '22:00';
        if (hora < 9) clave = '08:00';
        else if (hora < 11) clave = '10:00';
        else if (hora < 13) clave = '12:00';
        else if (hora < 15) clave = '14:00';
        else if (hora < 17) clave = '16:00';
        else if (hora < 19) clave = '18:00';
        else if (hora < 21) clave = '20:00';

        mapValores.set(clave, (mapValores.get(clave) || 0) + (Number(v.totalPagar) || 0));
      });

    } else if (diffDias <= 8) {
      const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
      let curr = new Date(dInicio);

      while (curr <= dFin) {
        const key = `${diasSemana[curr.getDay()]} ${curr.getDate()}`;
        mapValores.set(key, 0);
        labels.push(key);
        curr.setDate(curr.getDate() + 1);
      }

      lista.forEach(v => {
        if ((v.estado || '').toString().toUpperCase() === 'CANCELADO') return;
        const fecha = this.obtenerFechaObjeto(v);
        if (!fecha) return;

        const key = `${diasSemana[fecha.getDay()]} ${fecha.getDate()}`;
        if (mapValores.has(key)) {
          mapValores.set(key, (mapValores.get(key) || 0) + (Number(v.totalPagar) || 0));
        }
      });

    } else {
      let curr = new Date(dInicio);

      while (curr <= dFin) {
        const dia = curr.getDate().toString().padStart(2, '0');
        const mes = (curr.getMonth() + 1).toString().padStart(2, '0');
        const key = `${dia}/${mes}`;
        mapValores.set(key, 0);
        labels.push(key);
        curr.setDate(curr.getDate() + 1);
      }

      lista.forEach(v => {
        if ((v.estado || '').toString().toUpperCase() === 'CANCELADO') return;
        const fecha = this.obtenerFechaObjeto(v);
        if (!fecha) return;

        const dia = fecha.getDate().toString().padStart(2, '0');
        const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');
        const key = `${dia}/${mes}`;
        if (mapValores.has(key)) {
          mapValores.set(key, (mapValores.get(key) || 0) + (Number(v.totalPagar) || 0));
        }
      });
    }

    const data = labels.map(lbl => mapValores.get(lbl) || 0);
    return { labels, data };
  }

  private crearGraficoLinea(labels: string[], data: number[]): void {
    const canvas = document.getElementById('chartVentasLinea') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.chartLinea) this.chartLinea.destroy();

    this.chartLinea = new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Ventas (S/.)',
          data,
          borderColor: '#0284c7',
          backgroundColor: 'rgba(2, 132, 199, 0.12)',
          fill: true,
          tension: 0.35,
          pointBackgroundColor: '#0284c7',
          pointRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: '#f1f5f9' } },
          x: { grid: { display: false } }
        }
      }
    });
  }

  private crearGraficoMetodosPie(labels: string[], data: number[]): void {
    const canvas = document.getElementById('chartMetodosPie') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.chartMetodosPie) this.chartMetodosPie.destroy();

    this.chartMetodosPie = new Chart(canvas, {
      type: 'pie',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: ['#0284c7', '#38bdf8', '#f59e0b', '#10b981'],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom' } }
      }
    });
  }

  private crearGraficoProductosPie(labels: string[], data: number[]): void {
    const canvas = document.getElementById('chartProductosPie') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.chartProductosPie) this.chartProductosPie.destroy();

    this.chartProductosPie = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: ['#10b981', '#f59e0b', '#8b5cf6', '#ec4899', '#0284c7', '#64748b'],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom' } }
      }
    });
  }

  private destruirGraficos(): void {
    if (this.chartLinea) this.chartLinea.destroy();
    if (this.chartMetodosPie) this.chartMetodosPie.destroy();
    if (this.chartProductosPie) this.chartProductosPie.destroy();
  }

  // --- EXPORTACIÓN A EXCEL / CSV ---

  private descargarExcel(datos: any[], nombreArchivo: string): void {
    if (!datos || datos.length === 0) {
      alert('No hay datos disponibles para exportar.');
      return;
    }
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(datos);
    const workbook: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Datos');
    XLSX.writeFile(workbook, `${nombreArchivo}.xlsx`);
  }

  exportSectionData(): void {
    if (this.section === 'empleados') {
      this.exportEmpleadosCsv();
    } else if (this.section === 'productos') {
      this.exportProductosCsv();
    } else if (this.section === 'ventas') {
      this.exportVentasCsv();
    } else {
      this.exportarExcelVentas();
    }
  }

  exportEmpleadosCsv(): void {
    const datosExportar = this.empleados.map(e => ({
      ID: e.idEmpleado || e.id,
      Nombre: e.nombre,
      Rol: e.rol || 'N/A',
      Estado: (e.activo !== false && e.estado !== false) ? 'Activo' : 'Inactivo'
    }));
    this.descargarExcel(datosExportar, 'Reporte_Empleados');
  }

  exportProductosCsv(): void {
    this.exportarExcelProductos();
  }

  exportVentasCsv(): void {
    this.exportarExcelVentas();
  }

  exportarExcelVentas(): void {
    const datosExportar = this.ventasFiltradasActuales.map(v => ({
      ID: v.idPedido || v.id,
      Mesa: v.mesa || 'N/A',
      Tipo: v.tipoPedido || 'N/A',
      MetodoPago: v.metodoPago || 'N/A',
      Estado: v.estado || 'N/A',
      Total: v.totalPagar,
      Fecha: this.formatearFecha(v)
    }));
    this.descargarExcel(datosExportar, 'Reporte_Ventas');
  }

  exportarExcelProductos(): void {
    const todosProds: any[] = [];
    this.productosAgrupadosPorCategoria.forEach(grupo => {
      grupo.items.forEach(p => {
        todosProds.push({
          Categoria: grupo.categoria,
          ID: p.idProducto,
          Nombre: p.nombre,
          Descripcion: p.descripcion || '',
          Precio: p.precioBase,
          Disponible: p.disponible ? 'Sí' : 'No'
        });
      });
    });
    this.descargarExcel(todosProds, 'Reporte_Productos');
  }
}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductoService } from '../../service/producto-service';
import { CarritoService } from '../../service/carrito-service';
import { ClienteService } from '../../service/cliente.service';
import { ModalClienteService } from '../../service/modal-cliente.service';
import { NotificationService } from '../../service/notification.service';
import { AcompanamientoService } from '../../service/acompanamiento-service';

/**
 * ✅ Componente Productos - Público
 * Verifica login ANTES de agregar al carrito
 */
@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto.html',
  styleUrls: ['./producto.css'],
})
export class Producto implements OnInit {
  listProducto: any[] = [];
  listSalsas: { [idProducto: string]: any[] } = {};
  listTopping: { [idProducto: string]: any[] } = {};
  selectedSalsas: { [idProducto: string]: string[] } = {};
  selectedToppings: { [idProducto: string]: string[] } = {};
  mostrarExtras: { [idProducto: string]: boolean } = {};
  productoMostrandoExtras: string | null = null;
  idCategoria!: string;
  readonly ID_WAFFLE = 'dddddddd-dddd-dddd-dddd-dddddddddddd';
  
  limits: { [idProducto: string]: { maxSalsas: number; maxToppings: number } } = {
    'p0000050-0000-0000-0000-000000000050': { maxSalsas: 1, maxToppings: 1 },
    'p0000051-0000-0000-0000-000000000051': { maxSalsas: 1, maxToppings: 2 },
    'p0000052-0000-0000-0000-000000000052': { maxSalsas: 2, maxToppings: 3 },
    'p0000053-0000-0000-0000-000000000053': { maxSalsas: 3, maxToppings: 4 },
  };

  constructor(
    private productoService: ProductoService,
    private carrito: CarritoService,
    private clienteService: ClienteService,
    private modalClienteService: ModalClienteService,
    private notificationService: NotificationService,
    private acompanamientoService: AcompanamientoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('idCategoria');
      if (id) {
        this.idCategoria = id;
        this.getAllProductos(id);
      }
    });
  }
  
  getAllProductos(idCategoria: string) {
    this.productoService.getByCategoria(idCategoria).subscribe({
      next: (response: any[]) => {
        this.listProducto = response;
        response.forEach((producto) => {
          this.cargarAcompanamientosPorProducto(producto.idProducto);
        });
      },
      error: (err) => console.error('Error al cargar productos:', err),
    });
  }

  cargarAcompanamientosPorProducto(idProducto: string) {
    this.acompanamientoService.getByProducto(idProducto).subscribe({
      next: (acompanamientos: any[]) => {
        this.procesarAcompanamientos(idProducto, acompanamientos);
      },
      error: () => {
        this.listSalsas[idProducto] = [];
        this.listTopping[idProducto] = [];
        this.selectedSalsas[idProducto] = [];
        this.selectedToppings[idProducto] = [];
      },
    });
  }

  procesarAcompanamientos(idProducto: string, acompanamientos: any[]) {
    if (!acompanamientos || acompanamientos.length === 0) {
      this.listSalsas[idProducto] = [];
      this.listTopping[idProducto] = [];
      this.selectedSalsas[idProducto] = [];
      this.selectedToppings[idProducto] = [];
      return;
    }

    const salsas = acompanamientos.filter(
      (a) => a.tipoAcompanamiento === 'SALSA' || a.tipoAcompanamiento === 'Salsa'
    );
    const toppings = acompanamientos.filter(
      (a) => a.tipoAcompanamiento === 'TOPPING' || a.tipoAcompanamiento === 'Topping'
    );

    this.listSalsas[idProducto] = salsas;
    this.listTopping[idProducto] = toppings;

    if (!this.selectedSalsas[idProducto]) {
      this.selectedSalsas[idProducto] = [];
    }
    if (!this.selectedToppings[idProducto]) {
      this.selectedToppings[idProducto] = [];
    }
  }

  mostrarOpciones(p: any) {
    if (this.idCategoria === this.ID_WAFFLE) {
      this.productoMostrandoExtras = p.idProducto;
      Object.keys(this.mostrarExtras).forEach((key) => {
        if (key !== p.idProducto) {
          this.mostrarExtras[key] = false;
        }
      });

      if (!this.listSalsas[p.idProducto] || this.listSalsas[p.idProducto].length === 0) {
        this.acompanamientoService.getByProducto(p.idProducto).subscribe({
          next: (acompanamientos: any[]) => {
            this.procesarAcompanamientos(p.idProducto, acompanamientos);
            this.mostrarExtras[p.idProducto] = true;
          },
          error: () => {
            this.mostrarExtras[p.idProducto] = true;
          },
        });
      } else {
        this.mostrarExtras[p.idProducto] = true;
      }
    } else {
      this.agregarAlCarrito(p);
    }
  }

  confirmarAgregar(p: any) {
    const limit = this.limits[p.idProducto];
    const salsasSeleccionadas = this.selectedSalsas[p.idProducto]?.length || 0;
    const toppingsSeleccionados = this.selectedToppings[p.idProducto]?.length || 0;

    if (limit) {
      if (salsasSeleccionadas > limit.maxSalsas) {
        alert(`Máximo ${limit.maxSalsas} salsa(s) permitida(s)`);
        return;
      }
      if (toppingsSeleccionados > limit.maxToppings) {
        alert(`Máximo ${limit.maxToppings} topping(s) permitido(s)`);
        return;
      }
    }

    this.agregarAlCarrito(p);
    this.mostrarExtras[p.idProducto] = false;
    this.productoMostrandoExtras = null;
  }

  /**
   * ✅ AGREGAR AL CARRITO - Verifica login ANTES
   */
  private agregarAlCarrito(p: any) {
    // ✅ Verificar si cliente está logueado
    if (!this.clienteService.isClienteLogueado()) {
      this.modalClienteService.abrirModal();
      this.notificationService.warning('Por favor inicia sesión para comprar');
      return;
    }

    const salsasSeleccionadas = this.selectedSalsas[p.idProducto] || [];
    const toppingsSeleccionados = this.selectedToppings[p.idProducto] || [];
    const nombresSalsas = this.getNombresAcompanamientos(salsasSeleccionadas, p.idProducto, 'SALSA');
    const nombresToppings = this.getNombresAcompanamientos(toppingsSeleccionados, p.idProducto, 'TOPPING');

    const itemCarrito = {
      idProducto: p.idProducto,
      nombre: p.nombre,
      precioBase: p.precioBase,
      imagen: this.getImagenProducto(p),
      salsasIds: salsasSeleccionadas,
      toppingsIds: toppingsSeleccionados,
      salsas: nombresSalsas,
      toppings: nombresToppings,
      cantidad: 1,
      descripcion: p.descripcion,
    };

    this.carrito.agregar(itemCarrito);
    this.notificationService.success('Producto agregado al carrito ✅');
    this.selectedSalsas[p.idProducto] = [];
    this.selectedToppings[p.idProducto] = [];
  }

  private getNombresAcompanamientos(ids: string[], idProducto: string, tipo: string): string[] {
    if (!ids || ids.length === 0) return [];
    const lista = tipo === 'SALSA' ? this.listSalsas[idProducto] || [] : this.listTopping[idProducto] || [];
    return ids.map((id) => {
      const encontrado = lista.find((a) => a.idAcompanamiento === id);
      return encontrado ? encontrado.nombreAcompanamiento : 'Desconocido';
    });
  }

  toggleSalsa(idProducto: string, id: string) {
    if (!this.selectedSalsas[idProducto]) {
      this.selectedSalsas[idProducto] = [];
    }
    const lista = this.selectedSalsas[idProducto];
    const limit = this.limits[idProducto]?.maxSalsas ?? Number.POSITIVE_INFINITY;

    if (lista.includes(id)) {
      this.selectedSalsas[idProducto] = lista.filter((x) => x !== id);
    } else if (lista.length < limit) {
      lista.push(id);
    } else {
      alert(`Máximo ${limit} salsa(s) permitida(s) para este waffle`);
    }
  }

  toggleTopping(idProducto: string, id: string) {
    if (!this.selectedToppings[idProducto]) {
      this.selectedToppings[idProducto] = [];
    }
    const lista = this.selectedToppings[idProducto];
    const limit = this.limits[idProducto]?.maxToppings ?? Number.POSITIVE_INFINITY;

    if (lista.includes(id)) {
      this.selectedToppings[idProducto] = lista.filter((x) => x !== id);
    } else if (lista.length < limit) {
      lista.push(id);
    } else {
      alert(`Máximo ${limit} topping(s) permitido(s) para este waffle`);
    }
  }

  getImagenProducto(p: any): string {
    if (!p) return '/logo.png';
    let imagenUrl: string = typeof p === 'object' ? p.imagenUrl : null;

    if (imagenUrl && imagenUrl.trim() !== '') {
      if (imagenUrl.startsWith('http://') || imagenUrl.startsWith('https://')) {
        return imagenUrl;
      }
      const backendUrl = 'http://localhost:8081';
      const rutaLimpia = imagenUrl.startsWith('/') ? imagenUrl : '/' + imagenUrl;
      return `${backendUrl}${rutaLimpia}`;
    }

    const idProducto: string = typeof p === 'object' ? p.idProducto : p;
    if (!idProducto) return '/logo.png';
    const idLimpio = idProducto.trim().toLowerCase();

    const imagenes: { [key: string]: string } = {
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
      'p0000021-0000-0000-0000-00000000021': 'img/cafe/cafe-expresso.png',
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

    const ruta = imagenes[idLimpio];
    return ruta ? '/' + ruta : '/logo.png';
  }
}
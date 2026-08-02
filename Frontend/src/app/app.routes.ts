import { Routes } from '@angular/router';
import { Producto } from './component/producto/producto';
import { Carritos } from './component/carritos/carritos';
import { Categoria } from './component/categoria/categoria';
import { Pedido } from './component/pedido/pedido';
import { PanelEmpleado } from './component/panel-empleado/panel-empleado';
import { Login } from './component/login/login';
import { Estadistica } from './component/estadistica/estadistica';
import { PanelSuperadminComponent } from './component/panel-superadmin/panel-superadmin';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  // ✅ LOGIN EMPLEADO - Ruta pública
  { path: 'login', component: Login },

  // ✅ RUTAS PÚBLICAS (sin login)
  { 
    path: 'categoria', 
    component: Categoria
  },

  { 
    path: 'producto/:idCategoria', 
    component: Producto
  },

  // ✅ RUTAS CLIENTE (sin guard - usa ClienteService)
  { 
    path: 'pedido', 
    component: Pedido
  },

  // ✅ RUTAS PROTEGIDAS - EMPLEADO (roleGuard)
  { 
    path: 'empleado', 
    component: PanelEmpleado,
    canActivate: [roleGuard],
    data: { roles: ['EMPLEADO', 'SUPERADMIN'] }
  },

  { 
    path: 'carrito', 
    component: Carritos,
    canActivate: [roleGuard],
    data: { roles: ['EMPLEADO', 'SUPERADMIN'] }
  },

  { 
    path: 'estadistica', 
    component: Estadistica,
    canActivate: [roleGuard],
    data: { roles: ['EMPLEADO', 'SUPERADMIN'] }
  },

  // ✅ RUTAS PROTEGIDAS - SOLO SUPERADMIN
  { 
    path: 'panel-superadmin', 
    component: PanelSuperadminComponent,
    canActivate: [roleGuard],
    data: { roles: ['SUPERADMIN'] }
  },

  // ✅ Lazy load (PROTEGIDO - EMPLEADO)
  {
    path: 'estado-pedido',
    loadComponent: () =>
      import('./component/estado-pedido/estado-pedido')
        .then(m => m.EstadoPedidoComponent),
  },

  // ✅ Default redirect
  { path: '', redirectTo: 'categoria', pathMatch: 'full' },
  { path: '**', redirectTo: 'categoria' }
];
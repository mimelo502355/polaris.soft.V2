import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarritoService } from '../../service/carrito-service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-carritos',
  standalone: true,
  imports: [CommonModule,RouterLink],
  templateUrl: './carritos.html',
  styleUrl: './carritos.css',
})
export class Carritos {
   items: any[] = [];

  constructor(
    private carrito: CarritoService
  ) {}

  ngOnInit(): void {
    this.items = this.carrito.obtenerItems();
  }
  
  aumentar(index: number) {
    this.carrito.aumentar(index);
  }

  disminuir(index: number) {
    this.carrito.disminuir(index);
  }


  eliminar(index: number) {
    this.carrito.eliminar(index);
  }

  total() {
    return this.carrito.total();
  }
  
}

import { Component, OnInit } from '@angular/core';
import { CategoriaService } from '../../service/categoria-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

/**
 * ✅ Componente Categorías - Página pública
 */
@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categoria.html',
  styleUrl: './categoria.css',
})
export class Categoria implements OnInit {
  listCategoria: any[] = [];

  constructor(
    private router: Router,
    private categoryService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (response: any) => {
        this.listCategoria = response;
      },
      error: (error: any) => {
        console.error('Error al cargar categorías', error);
      },
    });
  }

  /**
   * Obtener ícono según categoría
   */
  getIcon(nombre: string): string {
    const icons: any = {
      Café: 'fa-coffee',
      frappe: 'fa-glass-whiskey',
      postres: 'fa-cookie-bite',
      Sandwiches: 'fa-hamburger',
      Bebidas: 'fa-glass-martini',
      ensalada: 'fa-apple-alt',
    };
    return icons[nombre] || 'fa-circle';
  }

  /**
   * Navegar a productos de la categoría
   */
  seeProduct(idCategoria: string): void {
    this.router.navigate(['/producto', idCategoria]);
  }
}
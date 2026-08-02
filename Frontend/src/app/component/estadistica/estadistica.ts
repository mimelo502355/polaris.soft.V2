import { Component } from '@angular/core';
import { EstadisticaService } from '../../service/estadistica-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js'; 

@Component({
  selector: 'app-estadistica',
  imports: [CommonModule, FormsModule],
  templateUrl: './estadistica.html',
  styleUrl: './estadistica.css',
})
export class Estadistica {
  totalHoy: number = 0;
  productos: any[] = [];
  inicio: string = '';
  fin: string = '';

  constructor(private estadisticaService: EstadisticaService) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    const hoy = new Date();
    const hace7Dias = new Date();
    hace7Dias.setDate(hoy.getDate() - 7);

    this.inicio = hace7Dias.toISOString().substring(0, 10); 
    this.fin = hoy.toISOString().substring(0, 10);

    this.buscar();
    this.cargarVentasSemana();
  }

  buscar() {
  this.estadisticaService.productoEstrella(this.inicio, this.fin)
    .subscribe(res => {
      const productos = res.map(p => p.nombre);
      const cantidades = res.map(p => p.totalVendido);

      new Chart('productoChart', {
        type: 'bar',
        data: {
          labels: productos,
          datasets: [{
            label: 'Cantidad vendida',
            data: cantidades,
          }]
        }
      });
    });
}


  cargarVentasSemana() {
    this.estadisticaService.ventasSemana()
      .subscribe(res => {
        const dias = res.map((d: any) => d.dia);
        const totales = res.map((d: any) => d.total);

        new Chart('ventasSemanaChart', {
          type: 'bar',
          data: {
            labels: dias,
            datasets: [{
              label: 'Ventas por día (S/.)',
              data: totales,
              backgroundColor: 'rgba(255, 99, 132, 0.5)',
              borderColor: 'rgba(255, 99, 132, 1)',
              borderWidth: 1
            }]
          },
          options: {
            responsive: true,
            plugins: {
              legend: { display: true },
              title: { display: true, text: 'Ventas Semanales' }
            },
            scales: {
              y: { beginAtZero: true }
            }
          }
        });
      });
  }
}

// src/app/components/dashboard/dashboard.component.ts
import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  inject,
  ChangeDetectorRef,
  AfterViewInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { Chart, registerables, ChartConfiguration } from 'chart.js';
import {
  DashboardService,
  DashboardData,
} from '../../services/dashboard.service';

// Inregistram toate componentele necesare din Chart.js
Chart.register(...registerables);

// Definim o interfata locala pentru a gestiona mai usor cardurile KPI in template
interface KpiCard {
  title: string;
  value: string;
  growth?: number;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {
  // ViewChild-uri pentru a obtine referinte catre elementele <canvas> din HTML
  @ViewChild('revenueChart')
  private revenueChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('categoryChart')
  private categoryChartRef!: ElementRef<HTMLCanvasElement>;

  // Injectarea serviciilor necesare
  private dashboardService = inject(DashboardService);
  private cdRef = inject(ChangeDetectorRef); // Necesar pentru a asigura actualizarea view-ului

  // Proprietati pentru gestionarea starii componentei
  private dataSubscription?: Subscription;
  private charts: Chart[] = [];

  public data: DashboardData | null = null;
  public isLoading = true;
  public error: string | null = null;
  public kpiCards: KpiCard[] = [];

  // La initializarea componentei, pornim incarcarea datelor
  ngOnInit(): void {
    this.loadData();
  }

  // Dupa ce view-ul este gata, randam graficele (daca datele au sosit deja)
  ngAfterViewInit(): void {
    if (this.data) {
      this.renderCharts();
    }
  }

  // La distrugerea componentei, facem curatenie
  ngOnDestroy(): void {
    this.dataSubscription?.unsubscribe();
    this.destroyCharts();
  }

  /**
   * Metoda principala care se aboneaza la serviciu pentru a primi datele.
   */
  public loadData(): void {
    this.isLoading = true;
    this.error = null;
    this.dataSubscription = this.dashboardService.getDashboardData().subscribe({
      next: (dashboardData) => {
        this.isLoading = false;
        this.data = dashboardData;
        this.prepareKpiCards(); // Pregatim datele pentru afisarea in carduri
        this.cdRef.detectChanges(); // Fortam o verificare a schimbarilor inainte de a desena
        this.renderCharts(); // Randam graficele cu noile date
      },
      error: (err) => {
        this.isLoading = false;
        this.error = 'A apărut o eroare la încărcarea datelor de pe server.';
        console.error('Eroare in dashboard.component:', err);
      },
    });
  }

  /**
   * Preia datele KPI din model si le transforma intr-un format usor de afisat in template.
   */
  private prepareKpiCards(): void {
    if (!this.data) return;
    const kpi = this.data.kpi;
    this.kpiCards = [
      {
        title: 'Venit Astăzi',
        value: `${kpi.todayRevenue.toFixed(2)} RON`,
        growth: kpi.revenueGrowth,
        icon: 'fa-dollar-sign',
        color: 'text-green-500',
      },
      {
        title: 'Comenzi Astăzi',
        value: kpi.todayOrders.toString(),
        growth: kpi.orderGrowth,
        icon: 'fa-shopping-cart',
        color: 'text-blue-500',
      },
      {
        title: 'Val. Medie/Comandă',
        value: `${kpi.averageOrderValue.toFixed(2)} RON`,
        icon: 'fa-balance-scale',
        color: 'text-yellow-500',
      },
      {
        title: 'Rezervări în Așteptare',
        value: kpi.pendingReservations.toString(),
        icon: 'fa-calendar-alt',
        color: 'text-purple-500',
      },
    ];
  }

  /**
   * Functia care orchestreaza desenarea tuturor graficelor.
   */
  private renderCharts(): void {
    // Ne asiguram ca datele si elementele canvas exista inainte de a desena
    if (!this.data || !this.revenueChartRef || !this.categoryChartRef) {
      return;
    }
    this.destroyCharts(); // Curatam graficele vechi pentru a evita suprapunerea

    this.charts.push(this.createRevenueChart());
    this.charts.push(this.createCategoryChart());
  }

  /**
   * Creeaza graficul pentru veniturile zilnice.
   */
  private createRevenueChart(): Chart {
    const chartData = this.data!.charts.dailyRevenue;
    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels: chartData.map((d) =>
          new Date(d.date).toLocaleDateString('ro-RO', { weekday: 'short' })
        ),
        datasets: [
          {
            label: 'Venit (RON)',
            data: chartData.map((d) => d.revenue),
            backgroundColor: 'rgba(59, 130, 246, 0.7)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 1,
            borderRadius: 4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true },
        },
      },
    };
    return new Chart(this.revenueChartRef.nativeElement, config);
  }

  /**
   * Creeaza graficul pentru vanzarile pe categorii.
   */
  private createCategoryChart(): Chart {
    const chartData = this.data!.charts.salesByCategory;
    const config: ChartConfiguration = {
      type: 'doughnut',
      data: {
        labels: chartData.map((c) => c.name),
        datasets: [
          {
            data: chartData.map((c) => c.value),
            backgroundColor: chartData.map((c) => c.color),
            hoverOffset: 8,
            borderWidth: 2,
            borderColor: '#f9fafb', // O margine alba intre felii
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { padding: 15 },
          },
        },
      },
    };
    return new Chart(this.categoryChartRef.nativeElement, config);
  }

  /**
   * Distruge instantele graficelor pentru a elibera memoria.
   */
  private destroyCharts(): void {
    this.charts.forEach((chart) => chart.destroy());
    this.charts = [];
  }

  /**
   * Functie utilitara pentru a returna clase CSS dinamice in template.
   */
  public getLogBadgeClass(type: string): string {
    const classes: { [key: string]: string } = {
      CREATE:
        'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300',
      UPDATE:
        'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
      DELETE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
    };
    return (
      classes[type] ||
      'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
    );
  }
}

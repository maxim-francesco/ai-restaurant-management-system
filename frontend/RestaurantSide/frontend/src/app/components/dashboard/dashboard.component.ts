import { Component, inject, type OnInit, type OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { Subscription } from 'rxjs';
import {
  DashboardService,
  type DashboardMetrics,
} from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  metrics: DashboardMetrics | null = null;
  isLoading = true;
  lastUpdated: Date | null = null;
  private subscription?: Subscription;

  private dashboardService = inject(DashboardService);

  ngOnInit(): void {
    this.subscription = this.dashboardService.metrics$.subscribe({
      next: (metrics) => {
        this.metrics = metrics;
        this.isLoading = false;
        this.lastUpdated = new Date();
      },
      error: (error) => {
        console.error('Dashboard metrics error:', error);
        this.isLoading = false;
      },
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  refreshData(): void {
    this.isLoading = true;
    this.dashboardService.refreshData().subscribe({
      next: () => {
        this.isLoading = false;
        this.lastUpdated = new Date();
      },
      error: (error) => {
        console.error('Manual refresh error:', error);
        this.isLoading = false;
      },
    });
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  }

  formatPercentage(value: number): string {
    return `${value >= 0 ? '+' : ''}${value.toFixed(1)}%`;
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString([], {
      month: 'short',
      day: 'numeric',
    });
  }

  getChangeIcon(value: number): string {
    if (value > 0) return 'fas fa-arrow-up';
    if (value < 0) return 'fas fa-arrow-down';
    return 'fas fa-minus';
  }

  getChangeColor(value: number): string {
    if (value > 0) return 'text-green-600 dark:text-green-400';
    if (value < 0) return 'text-red-600 dark:text-red-400';
    return 'text-gray-600 dark:text-gray-400';
  }

  getMaxValue(data: any[], key: string): number {
    return Math.max(...data.map((item) => item[key]));
  }

  getBarHeight(value: number, maxValue: number): number {
    return maxValue > 0 ? (value / maxValue) * 100 : 0;
  }

  getRevenueBarHeight(revenue: number): number {
    if (!this.metrics?.dailyRevenueData) return 0;
    const maxRevenue = this.getMaxValue(
      this.metrics.dailyRevenueData,
      'revenue'
    );
    return this.getBarHeight(revenue, maxRevenue);
  }

  getOrderBarHeight(orders: number): number {
    if (!this.metrics?.hourlyOrderData) return 0;
    const maxOrders = this.getMaxValue(this.metrics.hourlyOrderData, 'orders');
    return this.getBarHeight(orders, maxOrders);
  }

  getTotalRevenue(): number {
    if (!this.metrics?.dailyRevenueData) return 0;
    return this.metrics.dailyRevenueData.reduce(
      (sum, day) => sum + day.revenue,
      0
    );
  }

  getTotalDailyOrders(): number {
    if (!this.metrics?.hourlyOrderData) return 0;
    return this.metrics.hourlyOrderData.reduce(
      (sum, hour) => sum + hour.orders,
      0
    );
  }

  getPeakHourInfo(): string {
    if (!this.metrics?.peakHours || this.metrics.peakHours.length === 0) {
      return 'N/A';
    }
    const peak = this.metrics.peakHours[0];
    return `${peak.hour} (${peak.orderCount} orders)`;
  }

  formatOrderTime(orderDate: string | undefined): string {
    if (!orderDate) return 'N/A';
    return this.formatTime(new Date(orderDate));
  }
}

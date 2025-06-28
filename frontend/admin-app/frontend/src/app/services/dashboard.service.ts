// src/app/services/dashboard.service.ts
import { Injectable, inject } from '@angular/core';
import { combineLatest, type Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// Importam TOATE serviciile si modelele necesare
import { OrderService } from './order.service';
import { ProductService } from './product.service';
import { CategoryService } from './category.service';
import { ReservationService } from './reservation.service';
import { ActivityLogService } from './activity-log.service';

import type { OrderDTO } from '../models/order/order.model';
import type { Product } from '../models/product/product.model';
import type { Category } from '../models/product/category.model';
import type { ReservationResponse } from '../models/reservation/reservation-response.model';
import type { ActivityLog } from '../models/logs/activity-log.model';

// Definim un model de date complet pentru dashboard
export interface DashboardData {
  kpi: {
    todayRevenue: number;
    revenueGrowth: number;
    todayOrders: number;
    orderGrowth: number;
    averageOrderValue: number;
    pendingReservations: number;
  };
  charts: {
    dailyRevenue: { date: string; revenue: number; orders: number }[];
    salesByCategory: { name: string; value: number; color: string }[];
  };
  tables: {
    topProducts: { name: string; totalSold: number; totalRevenue: number }[];
    recentOrders: OrderDTO[];
    recentLogs: ActivityLog[];
  };
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private orderService = inject(OrderService);
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);
  private reservationService = inject(ReservationService);
  private activityLogService = inject(ActivityLogService);

  /**
   * Preia si proceseaza datele de la toate serviciile pentru a construi modelul dashboard-ului.
   */
  public getDashboardData(): Observable<DashboardData> {
    return combineLatest({
      orders: this.orderService.getAllOrders().pipe(catchError(() => of([]))),
      products: this.productService.getAll().pipe(catchError(() => of([]))),
      categories: this.categoryService.getAll().pipe(catchError(() => of([]))),
      reservations: this.reservationService
        .getAllReservations()
        .pipe(catchError(() => of([]))),
      logs: this.activityLogService.getLogs().pipe(catchError(() => of([]))),
    }).pipe(
      map(({ orders, products, categories, reservations, logs }) => {
        return this.processAllData(
          orders,
          products,
          categories,
          reservations,
          logs
        );
      })
    );
  }

  /**
   * Metoda centrala care orchestreaza calcularea tuturor metricilor.
   */
  private processAllData(
    orders: OrderDTO[],
    products: Product[],
    categories: Category[],
    reservations: ReservationResponse[],
    logs: ActivityLog[]
  ): DashboardData {
    const today = new Date();
    const yesterday = new Date();
    yesterday.setDate(today.getDate() - 1);

    const todayStart = this.getStartOfDay(today);
    const yesterdayStart = this.getStartOfDay(yesterday);
    const sevenDaysAgoStart = new Date();
    sevenDaysAgoStart.setDate(sevenDaysAgoStart.getDate() - 6);
    const sevenDaysAgo = this.getStartOfDay(sevenDaysAgoStart);

    // Filtram datele relevante
    const todayOrders = orders.filter(
      (o) => o.orderDate && new Date(o.orderDate) >= todayStart
    );
    const yesterdayOrders = orders.filter(
      (o) =>
        o.orderDate &&
        new Date(o.orderDate) >= yesterdayStart &&
        new Date(o.orderDate) < todayStart
    );
    const last7DaysOrders = orders.filter(
      (o) => o.orderDate && new Date(o.orderDate) >= sevenDaysAgo
    );

    // Calculam KPI-urile
    const todayRevenue = todayOrders.reduce(
      (sum, order) => sum + (order.totalAmount || 0),
      0
    );
    const yesterdayRevenue = yesterdayOrders.reduce(
      (sum, order) => sum + (order.totalAmount || 0),
      0
    );
    const revenueGrowth = this.calculateGrowth(todayRevenue, yesterdayRevenue);
    const orderGrowth = this.calculateGrowth(
      todayOrders.length,
      yesterdayOrders.length
    );
    const averageOrderValue =
      todayOrders.length > 0 ? todayRevenue / todayOrders.length : 0;
    const pendingReservations = reservations.filter(
      (r) => r.status === 'PENDING'
    ).length;

    return {
      kpi: {
        todayRevenue,
        revenueGrowth,
        todayOrders: todayOrders.length,
        orderGrowth,
        averageOrderValue,
        pendingReservations,
      },
      charts: {
        dailyRevenue: this.calculateDailyRevenue(last7DaysOrders),
        salesByCategory: this.calculateSalesByCategory(
          orders,
          products,
          categories
        ),
      },
      tables: {
        topProducts: this.calculateTopProducts(orders, products),
        recentOrders: orders
          .sort(
            (a, b) =>
              new Date(b.orderDate!).getTime() -
              new Date(a.orderDate!).getTime()
          )
          .slice(0, 5),
        recentLogs: logs
          .sort(
            (a, b) =>
              new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
          )
          .slice(0, 7),
      },
    };
  }

  // --- Functii Helper pentru Calcule ---

  private calculateGrowth(current: number, previous: number): number {
    if (previous === 0) return current > 0 ? 100 : 0;
    return ((current - previous) / previous) * 100;
  }

  private getStartOfDay(date: Date): Date {
    const newDate = new Date(date);
    newDate.setHours(0, 0, 0, 0);
    return newDate;
  }

  private calculateDailyRevenue(
    last7DaysOrders: OrderDTO[]
  ): { date: string; revenue: number; orders: number }[] {
    const dailyData: Map<string, { revenue: number; orders: number }> =
      new Map();
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      dailyData.set(this.getStartOfDay(d).toISOString().split('T')[0], {
        revenue: 0,
        orders: 0,
      });
    }
    last7DaysOrders.forEach((order) => {
      if (!order.orderDate) return;
      const dateStr = this.getStartOfDay(new Date(order.orderDate))
        .toISOString()
        .split('T')[0];
      if (dailyData.has(dateStr)) {
        const entry = dailyData.get(dateStr)!;
        entry.revenue += order.totalAmount || 0;
        entry.orders += 1;
      }
    });
    return Array.from(dailyData.entries()).map(([date, data]) => ({
      date,
      ...data,
    }));
  }

  private calculateSalesByCategory(
    orders: OrderDTO[],
    products: Product[],
    categories: Category[]
  ): { name: string; value: number; color: string }[] {
    const categoryRevenue: Map<number, number> = new Map();
    const categoryMap = new Map(categories.map((c) => [c.id!, c.name]));
    const colors = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

    orders.forEach((order) => {
      order.orderItems?.forEach((item) => {
        const product = products.find((p) => p.id === item.productId);
        if (product) {
          const revenue = (item.priceAtOrder || 0) * item.quantity;
          categoryRevenue.set(
            product.categoryId,
            (categoryRevenue.get(product.categoryId) || 0) + revenue
          );
        }
      });
    });

    return Array.from(categoryRevenue.entries())
      .map(([catId, value], index) => ({
        name: categoryMap.get(catId) || 'Necunoscut',
        value,
        color: colors[index % colors.length],
      }))
      .sort((a, b) => b.value - a.value);
  }

  private calculateTopProducts(
    orders: OrderDTO[],
    products: Product[]
  ): { name: string; totalSold: number; totalRevenue: number }[] {
    const productSales = new Map<number, { sold: number; revenue: number }>();
    orders.forEach((order) => {
      order.orderItems?.forEach((item) => {
        const current = productSales.get(item.productId) || {
          sold: 0,
          revenue: 0,
        };
        productSales.set(item.productId, {
          sold: current.sold + item.quantity,
          revenue: current.revenue + (item.priceAtOrder || 0) * item.quantity,
        });
      });
    });

    return (
      Array.from(productSales.entries())
        .map(([productId, data]) => {
          const product = products.find((p) => p.id === productId);
          // Corectia este aici:
          return {
            name: product?.name || 'Produs Șters',
            totalSold: data.sold,
            totalRevenue: data.revenue,
          };
        })
        // Si aici:
        .sort((a, b) => b.totalRevenue - a.totalRevenue)
        .slice(0, 5)
    );
  }
}

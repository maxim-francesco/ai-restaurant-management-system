import { Component, type OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  stats = [
    {
      title: 'Total Orders Today',
      value: '23',
      icon: 'fas fa-shopping-cart',
      color: 'blue',
      change: '+12%',
      changeType: 'increase',
    },
    {
      title: 'Total Revenue',
      value: '$1,247.50',
      icon: 'fas fa-dollar-sign',
      color: 'green',
      change: '+8.5%',
      changeType: 'increase',
    },
    {
      title: 'Active Tables',
      value: '8',
      icon: 'fas fa-chair',
      color: 'purple',
      change: '2 available',
      changeType: 'neutral',
    },
    {
      title: 'Pending Orders',
      value: '5',
      icon: 'fas fa-clock',
      color: 'orange',
      change: '-3 from yesterday',
      changeType: 'decrease',
    },
  ];

  topItems = [
    { name: 'Margherita Pizza', sales: 12, revenue: '$180.00' },
    { name: 'Caesar Salad', sales: 8, revenue: '$96.00' },
    { name: 'Grilled Salmon', sales: 6, revenue: '$144.00' },
    { name: 'Pasta Carbonara', sales: 5, revenue: '$75.00' },
    { name: 'Chocolate Cake', sales: 4, revenue: '$32.00' },
  ];

  recentActivities = [
    {
      id: 1,
      action: 'New order received',
      details: 'Table 5 - Margherita Pizza x2',
      timestamp: '2 minutes ago',
      icon: 'fas fa-plus-circle',
      color: 'green',
    },
    {
      id: 2,
      action: 'Order completed',
      details: 'Table 3 - Caesar Salad',
      timestamp: '5 minutes ago',
      icon: 'fas fa-check-circle',
      color: 'blue',
    },
    {
      id: 3,
      action: 'Reservation confirmed',
      details: 'John Smith - 4 guests at 7:00 PM',
      timestamp: '10 minutes ago',
      icon: 'fas fa-calendar-check',
      color: 'purple',
    },
    {
      id: 4,
      action: 'Low stock alert',
      details: 'Tomatoes - Only 5 units left',
      timestamp: '15 minutes ago',
      icon: 'fas fa-exclamation-triangle',
      color: 'orange',
    },
  ];

  ngOnInit(): void {
    // Simulate real-time updates
    this.startRealTimeUpdates();
  }

  private startRealTimeUpdates(): void {
    // Simulate periodic updates to dashboard data
    setInterval(() => {
      this.updateStats();
    }, 30000); // Update every 30 seconds
  }

  private updateStats(): void {
    // Simulate random updates to stats
    const randomIndex = Math.floor(Math.random() * this.stats.length);
    const stat = this.stats[randomIndex];

    if (stat.title === 'Total Orders Today') {
      const currentValue = Number.parseInt(stat.value);
      stat.value = (currentValue + Math.floor(Math.random() * 3)).toString();
    } else if (stat.title === 'Total Revenue') {
      const currentValue = Number.parseFloat(
        stat.value.replace('$', '').replace(',', '')
      );
      const newValue = currentValue + Math.random() * 50;
      stat.value = `$${newValue.toFixed(2)}`;
    }
  }

  getChangeIcon(changeType: string): string {
    switch (changeType) {
      case 'increase':
        return 'fas fa-arrow-up';
      case 'decrease':
        return 'fas fa-arrow-down';
      default:
        return 'fas fa-minus';
    }
  }

  getChangeColor(changeType: string): string {
    switch (changeType) {
      case 'increase':
        return 'text-green-600';
      case 'decrease':
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  }
}

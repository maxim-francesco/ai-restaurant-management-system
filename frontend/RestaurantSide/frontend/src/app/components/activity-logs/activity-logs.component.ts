import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-activity-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './activity-logs.component.html',
  styleUrls: ['./activity-logs.component.css'],
})
export class ActivityLogsComponent {
  selectedFilter = 'all';
  searchTerm = '';

  filters = [
    { value: 'all', label: 'All Activities' },
    { value: 'login', label: 'Login/Logout' },
    { value: 'product', label: 'Product Changes' },
    { value: 'reservation', label: 'Reservations' },
    { value: 'report', label: 'Reports' },
  ];

  activities = [
    {
      id: 1,
      type: 'login',
      action: 'User logged in',
      user: 'John Doe',
      timestamp: '2024-01-15 09:30:00',
      details: 'Successful login from IP 192.168.1.100',
      icon: 'fas fa-sign-in-alt',
      color: 'green',
    },
    {
      id: 2,
      type: 'product',
      action: 'Product updated',
      user: 'Jane Smith',
      timestamp: '2024-01-15 10:15:00',
      details: 'Updated "Margherita Pizza" price from $14.99 to $15.99',
      icon: 'fas fa-edit',
      color: 'blue',
    },
    {
      id: 3,
      type: 'reservation',
      action: 'New reservation added',
      user: 'Mike Johnson',
      timestamp: '2024-01-15 11:00:00',
      details:
        'Added reservation for "Robert Wilson" - 4 guests on 2024-01-16 19:00',
      icon: 'fas fa-calendar-plus',
      color: 'purple',
    },
    {
      id: 4,
      type: 'product',
      action: 'Product deleted',
      user: 'John Doe',
      timestamp: '2024-01-15 11:30:00',
      details: 'Deleted product "Old Menu Item"',
      icon: 'fas fa-trash',
      color: 'red',
    },
    {
      id: 5,
      type: 'report',
      action: 'Report generated',
      user: 'Jane Smith',
      timestamp: '2024-01-15 12:00:00',
      details: 'Generated daily sales report for 2024-01-14',
      icon: 'fas fa-file-alt',
      color: 'orange',
    },
  ];

  get filteredActivities() {
    let filtered = this.activities;

    if (this.selectedFilter !== 'all') {
      filtered = filtered.filter(
        (activity) => activity.type === this.selectedFilter
      );
    }

    if (this.searchTerm) {
      filtered = filtered.filter(
        (activity) =>
          activity.action
            .toLowerCase()
            .includes(this.searchTerm.toLowerCase()) ||
          activity.user.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
          activity.details.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    return filtered;
  }

  exportLogs() {
    console.log('Exporting activity logs...');
    // Simulate export functionality
    alert('Activity logs exported successfully!');
  }

  clearLogs() {
    if (
      confirm(
        'Are you sure you want to clear all activity logs? This action cannot be undone.'
      )
    ) {
      this.activities = [];
    }
  }
}

import { Component, inject, type OnInit, PLATFORM_ID } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth.service';
import { LoginService } from './services/login.service';
import type { UserResponse } from './models/user/user-response.model';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { LoginComponent } from './components/auth/login/login.component';
import { AiAssistantComponent } from './components/ai-assistant/ai-assistant.component';

interface NavigationItem {
  id: string;
  label: string;
  icon: string;
  roles: string[];
  route: string;
}

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  timestamp?: Date;
  read?: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [CommonModule, LoginComponent, AiAssistantComponent, RouterOutlet],
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  private serverUrl = 'http://localhost:8082';

  // Authentication state
  isLoggedIn = false;
  currentUser: UserResponse | null = null;
  userRole: string | null = null;

  // UI state
  currentView = 'dashboard';
  isDarkMode = false;
  showNotifications = false;

  // Navigation items with routes - Updated to include Orders
  navigationItems: NavigationItem[] = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: 'fas fa-tachometer-alt',
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/dashboard',
    },
    {
      id: 'products',
      label: 'Products',
      icon: 'fas fa-utensils',
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/products',
    },
    {
      id: 'orders',
      label: 'Orders',
      icon: 'fas fa-shopping-cart',
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/orders',
    },
    {
      id: 'reservations',
      label: 'Reservations',
      icon: 'fas fa-calendar-alt',
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/reservations',
    },
    {
      id: 'reports',
      label: 'Reports',
      icon: 'fas fa-chart-bar',
      roles: ['ADMIN'],
      route: '/reports',
    },
    {
      id: 'activity-logs',
      label: 'Activity Logs',
      icon: 'fas fa-history',
      roles: ['ADMIN'],
      route: '/activity-logs',
    },
    {
      id: 'profile',
      label: 'Profile',
      icon: 'fas fa-user',
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/profile',
    },
  ];

  // Notifications
  notifications: Notification[] = [
    {
      id: 1,
      message: 'Low stock alert: Tomatoes',
      timestamp: new Date(Date.now() - 2 * 60 * 1000),
      read: false,
      type: 'warning',
    },
    {
      id: 2,
      message: 'New reservation from John Smith',
      timestamp: new Date(Date.now() - 15 * 60 * 1000),
      read: false,
      type: 'info',
    },
    {
      id: 3,
      message: 'Daily report is ready',
      timestamp: new Date(Date.now() - 60 * 60 * 1000),
      read: true,
      type: 'success',
    },
  ];

  // Services using modern inject pattern
  private authService = inject(AuthService);
  private loginService = inject(LoginService);
  private router = inject(Router);

  private isBrowser = false;

  constructor() {
    this.isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  }

  ngOnInit(): void {
    // Subscribe to authentication state
    this.authService.isLoggedIn$.subscribe((isLoggedIn) => {
      this.isLoggedIn = isLoggedIn;
      if (isLoggedIn) {
        this.authService.currentUser$.subscribe((user) => {
          this.currentUser = user;
        });
        this.userRole = this.authService.getCurrentUserRole();
      } else {
        this.currentUser = null;
        this.userRole = null;
      }
    });

    // Set initial view based on current route
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        const currentRoute = this.router.url.split('/')[1] || 'dashboard';
        const navItem = this.navigationItems.find(
          (item) => item.route === `/${currentRoute}`
        );
        if (navItem) {
          this.currentView = navItem.id;
        }
      });

    this.loadThemePreference();
  }

  // Filter navigation items based on user role
  get filteredNavigationItems(): NavigationItem[] {
    if (!this.userRole) {
      return [];
    }
    return this.navigationItems.filter((item) =>
      item.roles.includes(this.userRole as string)
    );
  }

  // Set current view and navigate
  setCurrentView(view: string): void {
    this.currentView = view;
    const navItem = this.navigationItems.find((item) => item.id === view);
    if (navItem) {
      this.router.navigateByUrl(navItem.route);
    }
    this.showNotifications = false;
  }

  // Authentication methods
  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  // Theme methods
  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    this.applyTheme();
    this.saveThemePreference();
  }

  private applyTheme(): void {
    if (this.isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  private loadThemePreference(): void {
    if (this.isBrowser) {
      const savedTheme = localStorage.getItem('theme');
      this.isDarkMode = savedTheme === 'dark';
      this.applyTheme();
    } else {
      this.isDarkMode = false;
    }
  }

  private saveThemePreference(): void {
    if (this.isBrowser) {
      localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    }
  }

  // Notification methods
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  removeNotification(id: number): void {
    this.notifications = this.notifications.filter(
      (notification) => notification.id !== id
    );
  }

  /**
   * Constructs the full, absolute URL for the profile image
   */
  getProfileImageUrl(): string {
    if (this.currentUser?.profileImageUrl) {
      if (this.currentUser.profileImageUrl.startsWith('http')) {
        return this.currentUser.profileImageUrl;
      }
      return `${this.serverUrl}${this.currentUser.profileImageUrl}`;
    }
    return 'https://placehold.co/32x32/EBF4FF/76A9FA?text=No+Image';
  }
}

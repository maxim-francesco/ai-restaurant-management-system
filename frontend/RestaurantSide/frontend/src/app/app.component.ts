import { Component, inject, type OnInit, PLATFORM_ID } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth.service';
import { LoginService } from './services/login.service';
import type { UserResponse } from './models/user/user-response.model';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { LoginComponent } from './components/auth/login/login.component';
import { AiAssistantComponent } from './components/ai-assistant/ai-assistant.component';
// ProfileSettingsService is not directly used for the image URL logic anymore
// since that logic is duplicated in getProfileImageUrl() for consistency and simplicity.
// Remove if not used elsewhere in this component.
// import { ProfileSettingsService } from './services/profile-settings.service';

interface NavigationItem {
  id: string;
  label: string;
  icon: string;
  roles: string[];
  route: string;
}

// Re-using the Notification interface from ProfileSettingsComponent's pattern
// as it correctly includes the 'type' property used in HTML for styling.
interface Notification {
  id: number;
  message: string;
  // NOTE: This 'type' property is added based on observation from profile-settings.component.html
  // and is crucial for the notification styling you already have.
  type: 'success' | 'error' | 'warning' | 'info';
  timestamp?: Date; // Original app.component.ts had timestamp, profile-settings.component.ts did not for its notification interface
  read?: boolean; // Original app.component.ts had read, profile-settings.component.ts did not for its notification interface
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [CommonModule, LoginComponent, AiAssistantComponent, RouterOutlet],
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  // Backend server URL - Consistent with ProfileSettingsComponent's approach
  private serverUrl = 'http://localhost:8082';

  // Authentication state
  isLoggedIn = false;
  currentUser: UserResponse | null = null;
  userRole: string | null = null;

  // UI state
  currentView = 'dashboard';
  isDarkMode = false;
  showNotifications = false; // Original property

  // Navigation items with routes
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
      icon: 'fas fa-user', // This will be replaced by the image in HTML
      roles: ['ADMIN', 'EMPLOYEE'],
      route: '/profile',
    },
  ];

  // Notifications (Adjusted to match your existing HTML and ProfileSettings component's notification 'type')
  notifications: Notification[] = [
    {
      id: 1,
      message: 'Low stock alert: Tomatoes',
      timestamp: new Date(Date.now() - 2 * 60 * 1000),
      read: false,
      type: 'warning', // Added type for consistent styling with HTML
    },
    {
      id: 2,
      message: 'New reservation from John Smith',
      timestamp: new Date(Date.now() - 15 * 60 * 1000),
      read: false,
      type: 'info', // Added type for consistent styling with HTML
    },
    {
      id: 3,
      message: 'Daily report is ready',
      timestamp: new Date(Date.now() - 60 * 60 * 1000),
      read: true,
      type: 'success', // Added type for consistent styling with HTML
    },
  ];

  // Services using modern inject pattern
  private authService = inject(AuthService);
  private loginService = inject(LoginService);
  private router = inject(Router);
  // private profileSettingsService = inject(ProfileSettingsService); // No longer strictly needed for this specific task

  private isBrowser = false;

  constructor() {
    this.isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  }

  ngOnInit(): void {
    // Subscribe to authentication state
    this.authService.isLoggedIn$.subscribe((isLoggedIn) => {
      this.isLoggedIn = isLoggedIn;
      // When logged in, also subscribe to currentUser changes and get the role
      if (isLoggedIn) {
        this.authService.currentUser$.subscribe((user) => {
          this.currentUser = user;
        });
        // Correctly get the user role using the existing method from AuthService
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
    this.showNotifications = false; // Keep existing behavior
  }

  // Authentication methods
  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  // Theme methods (Restored to original presence)
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

  // Notification methods (Restored to original presence)
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  // NOTE: This method was present in your previous App.component.ts and used in its HTML snippet for notifications.
  removeNotification(id: number): void {
    this.notifications = this.notifications.filter(
      (notification) => notification.id !== id
    );
  }

  /**
   * Constructs the full, absolute URL for the profile image, consistent with ProfileSettingsComponent.
   * It prepends the backend server address to the relative path stored in the database.
   * @returns The absolute URL to the profile image or a placeholder.
   */
  getProfileImageUrl(): string {
    if (this.currentUser?.profileImageUrl) {
      // If the URL is already absolute (e.g., from a CDN), use it directly.
      if (this.currentUser.profileImageUrl.startsWith('http')) {
        return this.currentUser.profileImageUrl;
      }
      // Otherwise, prepend the backend server URL to the relative path.
      return `${this.serverUrl}${this.currentUser.profileImageUrl}`;
    }
    // Return a modern placeholder if no image URL is available.
    // Placeholder size is adjusted for sidebar icon.
    return 'https://placehold.co/32x32/EBF4FF/76A9FA?text=No+Image';
  }
}

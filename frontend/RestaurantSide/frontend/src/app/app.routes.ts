import { Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login/login.component';
import { ProductManagementComponent } from './components/product-management/product-management.component';
import { ReservationManagementComponent } from './components/reservation-management/reservation-management.component';
import { ProfileSettingsComponent } from './components/profile/profile-settings/profile-settings.component';
import { AuthGuard } from './services/auth-guard.service';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ReportsComponent } from './components/reports/reports.component';
import { ActivityLogsComponent } from './components/activity-logs/activity-logs.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin/dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'products',
    component: ProductManagementComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'reservations',
    component: ReservationManagementComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'profile',
    component: ProfileSettingsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'reports',
    component: ReportsComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'activity-logs',
    component: ActivityLogsComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'unauthorized',
    // Add an unauthorized component or redirect
    redirectTo: 'dashboard',
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];

import { Injectable, inject } from '@angular/core';
import { AuthService } from './auth.service';
import type { Observable } from 'rxjs';
import type { LoginRequest } from '../models/user/login-request.model';
import type { LoginResponse } from '../models/user/login-response.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  // Modern dependency injection using inject() function
  private authService = inject(AuthService);

  // Streamlined login service - only login functionality
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.authService.login(credentials);
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  // Get current user role for navigation/access control
  getCurrentUserRole(): 'ADMIN' | 'EMPLOYEE' | null {
    return this.authService.getCurrentUserRole();
  }

  // Logout functionality
  logout(): void {
    this.authService.logout();
  }
}

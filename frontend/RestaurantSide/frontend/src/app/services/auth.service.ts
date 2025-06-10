import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { type Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import type { LoginRequest } from '../models/user/login-request.model';
import type { LoginResponse } from '../models/user/login-response.model';
import type { UserResponse } from '../models/user/user-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8082/api/users';
  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  public currentUser$ = this.currentUserSubject.asObservable();
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  // Modern dependency injection using inject() function
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  constructor() {
    // Only check stored auth if we're in the browser
    if (this.isBrowser) {
      this.checkStoredAuth();
    }
  }

  // Login functionality
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap((response) => {
          if (response.userId) {
            this.setAuthState(response.userId, response.role);
            // Fetch full user details after successful login
            this.getUserById(response.userId).subscribe({
              next: (user) => this.currentUserSubject.next(user),
              error: (error) =>
                console.error('Error fetching user details:', error),
            });
          }
        })
      );
  }

  // Get user by ID
  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/${id}`);
  }

  // Logout functionality
  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('userId');
      localStorage.removeItem('userRole');
    }
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
  }

  // Get current user
  getCurrentUser(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return this.isLoggedInSubject.value;
  }

  // Get current user ID
  getCurrentUserId(): number | null {
    if (!this.isBrowser) {
      return null;
    }
    const userId = localStorage.getItem('userId');
    return userId ? Number.parseInt(userId, 10) : null;
  }

  // Get current user role
  getCurrentUserRole(): 'ADMIN' | 'EMPLOYEE' | null {
    if (!this.isBrowser) {
      return null;
    }
    const role = localStorage.getItem('userRole');
    return role as 'ADMIN' | 'EMPLOYEE' | null;
  }

  // Private methods
  private setAuthState(userId: number, role: 'ADMIN' | 'EMPLOYEE'): void {
    if (this.isBrowser) {
      localStorage.setItem('userId', userId.toString());
      localStorage.setItem('userRole', role);
    }
    this.isLoggedInSubject.next(true);
  }

  private checkStoredAuth(): void {
    if (!this.isBrowser) {
      return;
    }

    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');

    if (userId && userRole) {
      this.isLoggedInSubject.next(true);
      // Fetch current user details
      this.getUserById(Number.parseInt(userId, 10)).subscribe({
        next: (user) => this.currentUserSubject.next(user),
        error: () => this.logout(), // Clear invalid stored auth
      });
    }
  }
}

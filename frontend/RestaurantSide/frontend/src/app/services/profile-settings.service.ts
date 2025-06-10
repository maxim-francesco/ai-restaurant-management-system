import { Injectable, inject } from '@angular/core';
import { HttpClient, type HttpEvent } from '@angular/common/http';
import type { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import type { UserResponse } from '../models/user/user-response.model';
import type { UpdateUserRequest } from '../models/user/update-user.model';
import type { UploadResponse } from '../models/user/upload-response.model';

@Injectable({
  providedIn: 'root',
})
export class ProfileSettingsService {
  private apiUrl = 'http://localhost:8082/api/users';

  // Modern dependency injection using inject() function
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  // Get current user profile
  getCurrentUserProfile(): Observable<UserResponse> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      throw new Error('No authenticated user found');
    }
    return this.authService.getUserById(userId);
  }

  // Update user profile
  updateUserProfile(updateData: UpdateUserRequest): Observable<UserResponse> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      throw new Error('No authenticated user found');
    }

    return this.http
      .put<UserResponse>(`${this.apiUrl}/${userId}`, updateData)
      .pipe(
        tap((updatedUser) => {
          // Update the current user in AuthService
          this.authService['currentUserSubject'].next(updatedUser);
        })
      );
  }

  // Upload profile image
  uploadProfileImage(file: File): Observable<HttpEvent<UploadResponse>> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      throw new Error('No authenticated user found');
    }

    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<UploadResponse>(
      `${this.apiUrl}/${userId}/upload-image`,
      formData,
      {
        reportProgress: true,
        observe: 'events',
      }
    );
  }

  // Get user profile image URL
  getProfileImageUrl(): string | null {
    const currentUser = this.authService.getCurrentUser();
    return currentUser?.profileImageUrl || null;
  }

  // Update profile image URL after successful upload
  updateProfileImageUrl(imageUrl: string): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      const updatedUser = { ...currentUser, profileImageUrl: imageUrl };
      this.authService['currentUserSubject'].next(updatedUser);
    }
  }

  // Validate if current user can perform admin actions
  canPerformAdminActions(): boolean {
    return this.authService.getCurrentUserRole() === 'ADMIN';
  }

  // Get user role for UI display
  getUserRole(): 'ADMIN' | 'EMPLOYEE' | null {
    return this.authService.getCurrentUserRole();
  }

  // Check if profile belongs to current user
  isOwnProfile(userId: number): boolean {
    return this.authService.getCurrentUserId() === userId;
  }
}

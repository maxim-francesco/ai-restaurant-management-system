import { Component, inject, type OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileSettingsService } from '../../../services/profile-settings.service';
import type { UserResponse } from '../../../models/user/user-response.model';
import type { UpdateUserRequest } from '../../../models/user/update-user.model';
import { HttpEventType } from '@angular/common/http';

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Component({
  selector: 'app-profile-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-settings.component.html',
  styleUrls: ['./profile-settings.component.css'],
})
export class ProfileSettingsComponent implements OnInit {
  // Backend server URL
  private serverUrl = 'http://localhost:8082';

  // User data
  currentUser: UserResponse | null = null;
  editableUser: UpdateUserRequest = {};

  // UI state
  isEditing = false;
  isLoading = true; // Start in loading state
  isSaving = false;

  // Image upload
  selectedFile: File | null = null;
  uploadProgress = 0;
  isUploading = false;
  imagePreview: string | ArrayBuffer | null = null;

  // Notification system
  notifications: Notification[] = [];
  private notificationIdCounter = 1;

  // Services
  private profileService = inject(ProfileSettingsService);

  ngOnInit(): void {
    this.loadUserProfile();
  }

  // Load current user profile
  loadUserProfile(): void {
    this.isLoading = true;
    this.profileService.getCurrentUserProfile().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.resetEditableUser();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading user profile:', error);
        this.showNotification('Failed to load profile information', 'error');
        this.isLoading = false;
      },
    });
  }

  // Reset editable user data
  resetEditableUser(): void {
    if (this.currentUser) {
      this.editableUser = {
        name: this.currentUser.name,
        phone: this.currentUser.phone,
        role: this.currentUser.role,
      };
    }
  }

  // Toggle edit mode
  toggleEditMode(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.resetEditableUser();
    }
  }

  // Save profile changes
  saveProfile(): void {
    if (!this.isFormValid()) {
      this.showNotification('Please fill in all required fields', 'error');
      return;
    }

    this.isSaving = true;

    const updateData: UpdateUserRequest = {};
    if (this.editableUser.name !== this.currentUser?.name) {
      updateData.name = this.editableUser.name;
    }
    if (this.editableUser.phone !== this.currentUser?.phone) {
      updateData.phone = this.editableUser.phone;
    }
    if (
      this.profileService.canPerformAdminActions() &&
      this.editableUser.role !== this.currentUser?.role
    ) {
      updateData.role = this.editableUser.role;
    }

    if (Object.keys(updateData).length === 0) {
      this.isEditing = false;
      this.isSaving = false;
      this.showNotification('No changes to save', 'info');
      return;
    }

    this.profileService.updateUserProfile(updateData).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;
        this.isEditing = false;
        this.isSaving = false;
        this.showNotification('Profile updated successfully!', 'success');
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.showNotification('Failed to update profile', 'error');
        this.isSaving = false;
      },
    });
  }

  // Handle file selection
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (!file.type.startsWith('image/')) {
        this.showNotification('Please select a valid image file', 'error');
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        // 5MB
        this.showNotification('File size must be less than 5MB', 'error');
        return;
      }
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  // Upload profile image
  uploadImage(): void {
    if (!this.selectedFile) {
      this.showNotification('Please select an image first', 'error');
      return;
    }

    this.isUploading = true;
    this.uploadProgress = 0;

    this.profileService.uploadProfileImage(this.selectedFile).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.isUploading = false;
          this.selectedFile = null;
          this.imagePreview = null;
          this.showNotification(
            'Profile image updated successfully!',
            'success'
          );
          // Reload profile to get the new image URL from the server state
          this.loadUserProfile();
        }
      },
      error: (error) => {
        console.error('Error uploading image:', error);
        this.showNotification('Failed to upload image', 'error');
        this.isUploading = false;
        this.uploadProgress = 0;
      },
    });
  }

  // Cancel image selection
  cancelImageSelection(): void {
    this.selectedFile = null;
    this.imagePreview = null;
  }

  // Form validation
  isFormValid(): boolean {
    return !!(
      this.editableUser.name?.trim() && this.editableUser.phone?.trim()
    );
  }

  // Check if user can edit role
  canEditRole(): boolean {
    return this.profileService.canPerformAdminActions();
  }

  /**
   * *** FIX: Constructs the full, absolute URL for the profile image. ***
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
    return 'https://placehold.co/150x150/EBF4FF/76A9FA?text=No+Image';
  }

  // Notification methods
  showNotification(
    message: string,
    type: 'success' | 'error' | 'warning' | 'info' = 'success'
  ): void {
    const notification: Notification = {
      id: this.notificationIdCounter++,
      message,
      type,
    };
    this.notifications.push(notification);
    setTimeout(() => this.removeNotification(notification.id), 5000);
  }

  removeNotification(id: number): void {
    this.notifications = this.notifications.filter((n) => n.id !== id);
  }
}

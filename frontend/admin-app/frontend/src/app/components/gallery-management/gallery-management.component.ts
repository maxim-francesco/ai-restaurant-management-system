import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GalleryService } from '../../services/gallery.service';
import { EventCategory } from '../../models/gallery/event-category.model';

// Interfața pentru notificări, copiată pentru consistență
interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Component({
  selector: 'app-gallery-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gallery-management.component.html',
  styleUrls: ['./gallery-management.component.css'],
})
export class GalleryManagementComponent implements OnInit {
  // Stări de încărcare
  isLoading = false;
  isSaving = false;

  // Stări pentru modale
  showCategoryModal = false;
  showEventModal = false;
  showDeleteConfirmModal = false;

  // Date
  categories: EventCategory[] = [];

  // Formulare (model-driven, pentru consistență)
  newCategoryName = '';
  editingCategoryId: number | null = null;

  newEventName = '';
  selectedFile: File | null = null;
  eventModalCategoryId: number | null = null; // Categoria pentru care adăugăm un eveniment

  // Obiect pentru confirmarea ștergerii
  deleteItem: { type: 'category' | 'event'; id: number; name: string } | null =
    null;

  // Notificări
  notifications: Notification[] = [];
  private notificationIdCounter = 1;

  constructor(public galleryService: GalleryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.galleryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Eroare la încărcarea categoriilor:', err);
        this.showNotification(
          'Nu s-au putut încărca datele galeriei.',
          'error'
        );
        this.isLoading = false;
      },
    });
  }

  // --- Logică Modale Categorie ---
  openCategoryModal(category?: EventCategory): void {
    if (category) {
      this.editingCategoryId = category.id;
      this.newCategoryName = category.name;
    } else {
      this.editingCategoryId = null;
      this.newCategoryName = '';
    }
    this.showCategoryModal = true;
  }

  closeCategoryModal(): void {
    this.showCategoryModal = false;
  }

  saveCategory(): void {
    if (!this.newCategoryName.trim()) return;
    this.isSaving = true;

    // Aici ar trebui să implementezi și logica de UPDATE în service/backend dacă vrei editare.
    // Deocamdată, ne concentrăm pe adăugare, conform microserviciului creat.
    if (this.editingCategoryId) {
      this.showNotification(
        'Funcționalitatea de editare nu este implementată încă.',
        'info'
      );
      this.isSaving = false;
      return;
    }

    this.galleryService
      .createCategory({ name: this.newCategoryName })
      .subscribe({
        next: () => {
          this.showNotification('Categorie adăugată cu succes!');
          this.isSaving = false;
          this.closeCategoryModal();
          this.loadCategories();
        },
        error: (err) => {
          this.showNotification('Eroare la salvarea categoriei.', 'error');
          this.isSaving = false;
        },
      });
  }

  // --- Logică Modale Eveniment ---
  openEventModal(categoryId: number): void {
    this.eventModalCategoryId = categoryId;
    this.newEventName = '';
    this.selectedFile = null;
    this.showEventModal = true;
  }

  closeEventModal(): void {
    this.showEventModal = false;
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] ?? null;
  }

  saveEvent(): void {
    if (
      !this.newEventName.trim() ||
      !this.selectedFile ||
      !this.eventModalCategoryId
    ) {
      this.showNotification('Toate câmpurile sunt obligatorii.', 'error');
      return;
    }
    this.isSaving = true;

    this.galleryService
      .addEvent(this.eventModalCategoryId, this.newEventName, this.selectedFile)
      .subscribe({
        next: () => {
          this.showNotification('Eveniment adăugat cu succes!');
          this.isSaving = false;
          this.closeEventModal();
          this.loadCategories();
        },
        error: (err) => {
          this.showNotification('Eroare la salvarea evenimentului.', 'error');
          this.isSaving = false;
        },
      });
  }

  // --- Logică Ștergere ---
  confirmDelete(
    type: 'category' | 'event',
    item: { id: number; name: string }
  ): void {
    this.deleteItem = { type, id: item.id, name: item.name };
    this.showDeleteConfirmModal = true;
  }

  cancelDelete(): void {
    this.showDeleteConfirmModal = false;
    this.deleteItem = null;
  }

  executeDelete(): void {
    if (!this.deleteItem) return;
    const { type, id, name } = this.deleteItem;

    const serviceCall =
      type === 'category'
        ? this.galleryService.deleteCategory(id)
        : this.galleryService.deleteEvent(id);

    serviceCall.subscribe({
      next: () => {
        this.showNotification(
          `${
            type === 'category' ? 'Categoria' : 'Evenimentul'
          } "${name}" a fost șters/șters.`
        );
        this.loadCategories();
      },
      error: (err) => {
        this.showNotification(`Eroare la ștergerea elementului.`, 'error');
        console.error(err);
      },
      complete: () => {
        this.cancelDelete();
      },
    });
  }

  // --- Logică Notificări ---
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
    setTimeout(() => this.removeNotification(notification.id), 3000);
  }

  removeNotification(id: number): void {
    this.notifications = this.notifications.filter((n) => n.id !== id);
  }
}

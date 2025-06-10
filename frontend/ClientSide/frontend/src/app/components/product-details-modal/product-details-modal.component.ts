import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  image: string;
  category: string;
  ingredients: string[];
  allergens: string[];
  dietary: string[];
}

@Component({
  selector: 'app-product-details-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-details-modal.component.html',
  styleUrls: ['./product-details-modal.component.css'],
})
export class ProductDetailsModalComponent {
  @Input() item!: MenuItem;
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();

  isFavorite = false;
  customerNotes = '';

  closeModal() {
    this.close.emit();
  }

  toggleFavorite() {
    this.isFavorite = !this.isFavorite;
  }

  onBackdropClick(event: Event) {
    if (event.target === event.currentTarget) {
      this.closeModal();
    }
  }
}

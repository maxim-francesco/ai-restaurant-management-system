// src/app/gallery/gallery.component.ts

import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  AfterViewInit,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GalleryService } from '../../services/gallery.service';
import { EventCategory } from '../../models/gallery/event-category.model';
import { Event as GalleryEvent } from '../../models/gallery/event.model';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-gallery',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule],
  templateUrl: './gallery.component.html',
  styleUrls: ['./gallery.component.css'],
})
export class GalleryComponent implements OnInit, AfterViewInit {
  // --- Logică pentru caruselul de categorii ---
  @ViewChild('categoryScroller') categoryScroller!: ElementRef;
  showCategoryPrev = false;
  showCategoryNext = true;
  // --- PROPRIETATE NOUĂ pentru a controla centrarea ---
  areCategoriiDerulabile = false;

  // --- Logică pentru caruselul de imagini ---
  @ViewChild('galleryGridScroller') galleryGridScroller!: ElementRef;
  showGalleryPrev = false;
  showGalleryNext = true;

  public isLoading = true;
  public allCategories: EventCategory[] = [];
  public selectedCategoryId: number | 'all' = 'all';
  public lightboxImage: GalleryEvent | null = null;

  constructor(public galleryService: GalleryService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.galleryService.getAllCategoriesWithEvents().subscribe({
      next: (data) => {
        this.allCategories = data;
        this.isLoading = false;
        this.checkAllScrollsWithDelay();
      },
      error: (err) => {
        console.error('A apărut o eroare la preluarea galeriei:', err);
        this.isLoading = false;
      },
    });
  }

  ngAfterViewInit(): void {
    this.checkAllScrollsWithDelay();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.checkAllScrolls();
  }

  private checkAllScrolls(): void {
    this.checkCategoryScroll();
    this.checkGalleryScroll();
  }

  private checkAllScrollsWithDelay(): void {
    setTimeout(() => this.checkAllScrolls(), 100);
  }

  // --- Metoda actualizată pentru caruselul de categorii ---
  checkCategoryScroll(): void {
    if (!this.categoryScroller?.nativeElement) return;
    const el = this.categoryScroller.nativeElement;
    // Verificăm dacă lățimea conținutului depășește lățimea vizibilă
    this.areCategoriiDerulabile = el.scrollWidth > el.clientWidth;

    // Afișăm săgețile doar dacă este derulabil
    this.showCategoryPrev = this.areCategoriiDerulabile && el.scrollLeft > 5;
    this.showCategoryNext =
      this.areCategoriiDerulabile &&
      el.scrollLeft < el.scrollWidth - el.clientWidth - 5;
  }

  scrollCategory(amount: number): void {
    this.categoryScroller.nativeElement.scrollBy({
      left: amount,
      behavior: 'smooth',
    });
    setTimeout(() => this.checkCategoryScroll(), 400);
  }

  // --- Metode pentru caruselul de imagini (neschimbate) ---
  checkGalleryScroll(): void {
    if (!this.galleryGridScroller?.nativeElement) return;
    const el = this.galleryGridScroller.nativeElement;
    this.showGalleryPrev = el.scrollLeft > 5;
    this.showGalleryNext = el.scrollLeft < el.scrollWidth - el.clientWidth - 5;
  }

  scrollGallery(amount: number): void {
    this.galleryGridScroller.nativeElement.scrollBy({
      left: amount,
      behavior: 'smooth',
    });
    setTimeout(() => this.checkGalleryScroll(), 400);
  }

  // --- Restul metodelor componentei ---
  get filteredImages(): GalleryEvent[] {
    if (this.selectedCategoryId === 'all') {
      return this.allCategories.flatMap((category) => category.events);
    }
    const selectedCategory = this.allCategories.find(
      (category) => category.id === this.selectedCategoryId
    );
    return selectedCategory ? selectedCategory.events : [];
  }

  public getTotalImageCount(): number {
    if (!this.allCategories) return 0;
    return this.allCategories.reduce(
      (total, category) => total + category.events.length,
      0
    );
  }

  filterByCategory(categoryId: number | 'all'): void {
    this.selectedCategoryId = categoryId;
    this.checkAllScrollsWithDelay();
  }

  openLightbox(image: GalleryEvent): void {
    this.lightboxImage = image;
    document.body.style.overflow = 'hidden';
  }

  closeLightbox(): void {
    this.lightboxImage = null;
    document.body.style.overflow = 'auto';
  }

  getIconForCategory(categoryName: string): string {
    const name = categoryName.toLowerCase();
    if (name.includes('nunt')) return 'favorite';
    if (name.includes('botez')) return 'child_care';
    if (name.includes('corporate')) return 'business_center';
    if (name.includes('cibo') || name.includes('food')) return 'restaurant';
    if (name.includes('interni') || name.includes('interior')) return 'chair';
    return 'celebration';
  }
}

import {
  Component,
  type OnInit,
  ViewChild,
  ElementRef,
  AfterViewInit,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import {
  MenuService,
  MenuItem,
  MenuCategory,
} from '../../services/menu.service';
import { N8nIntegrationService } from '../../services/n8n-integration.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent implements OnInit, AfterViewInit {
  // Logica este păstrată DOAR pentru caruselul de categorii
  @ViewChild('categoryScroller') categoryScroller!: ElementRef;
  showPrevButton = false;
  showNextButton = true;

  searchTerm = '';
  selectedCategory = 'all';

  restaurantPhone = '+40 264 123 456';
  restaurantWhatsApp = '+40 264 123 456';

  categories: MenuCategory[] = [];
  menuItems: MenuItem[] = [];
  filteredItems: MenuItem[] = [];
  isLoading = true;

  constructor(
    private menuService: MenuService,
    private n8nService: N8nIntegrationService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.menuService.getMenuData().subscribe({
      next: ({ menuItems, categories }) => {
        this.menuItems = menuItems;
        this.categories = categories;
        this.updateCategoryCounts();
        this.applyFilters();
        this.isLoading = false;
        setTimeout(() => this.checkCategoryScroll(), 50);
      },
      error: (err) => {
        console.error('Failed to load menu data', err);
        this.isLoading = false;
      },
    });
  }

  ngAfterViewInit(): void {
    this.checkCategoryScroll();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.checkCategoryScroll();
  }

  checkCategoryScroll(): void {
    if (!this.categoryScroller?.nativeElement) return;
    const element = this.categoryScroller.nativeElement;
    const maxScrollLeft = element.scrollWidth - element.clientWidth;
    this.showPrevButton = element.scrollLeft > 5;
    this.showNextButton = element.scrollLeft < maxScrollLeft - 5;
  }

  scrollCategory(amount: number): void {
    this.categoryScroller.nativeElement.scrollBy({
      left: amount,
      behavior: 'smooth',
    });
    // Așteptăm finalizarea animației înainte de a verifica din nou
    setTimeout(() => this.checkCategoryScroll(), 400);
  }

  filterByCategory(categoryId: string): void {
    this.selectedCategory = categoryId;
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = 'all';
    this.applyFilters();
  }

  private applyFilters(): void {
    let filtered = this.menuItems;

    if (this.selectedCategory !== 'all') {
      filtered = filtered.filter(
        (item) => item.category === this.selectedCategory
      );
    }

    if (this.searchTerm.trim()) {
      const searchLower = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (item) =>
          item.name.toLowerCase().includes(searchLower) ||
          item.description.toLowerCase().includes(searchLower)
      );
    }

    this.filteredItems = filtered;
  }

  // --- Restul metodelor rămân neschimbate ---
  private updateCategoryCounts(): void {
    this.categories.forEach((category) => {
      if (category.id === 'all') {
        category.count = this.menuItems.length;
      } else {
        category.count = this.menuItems.filter(
          (item) => item.category === category.id
        ).length;
      }
    });
  }
  callRestaurant(): void {
    console.log('Se inițiază declanșarea workflow-ului n8n...');
    this.n8nService.triggerWorkflow().subscribe({
      next: (response) => {
        console.log('Workflow-ul n8n a fost declanșat cu succes!', response);
        // Opțional: poți afișa un mesaj de succes utilizatorului
        alert(
          'Perfect! Veți fi apelat în câteva momente pentru a prelua comanda.'
        );
      },
      error: (err) => {
        console.error('A apărut o eroare la declanșarea workflow-ului:', err);
        // Opțional: poți afișa un mesaj de eroare
        alert(
          'Serviciul de comenzi telefonice este momentan indisponibil. Vă rugăm să încercați mai târziu.'
        );
      },
    });
  }
  openWhatsApp(): void {
    const message = encodeURIComponent(
      'Bună ziua! Aș dori să fac o comandă de la Ristorante Bella Vista.'
    );
    window.open(
      `https://wa.me/${this.restaurantWhatsApp.replace(
        /\s+/g,
        ''
      )}?text=${message}`,
      '_blank'
    );
  }
  trackByCategory(index: number, category: MenuCategory): string {
    return category.id;
  }
  trackByMenuItem(index: number, item: MenuItem): number {
    return item.id;
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductDetailsModalComponent } from '../product-details-modal/product-details-modal.component';

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
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, ProductDetailsModalComponent],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent {
  selectedCategory = 'all';
  selectedDietaryFilter = 'all';
  selectedItem: MenuItem | null = null;
  isModalOpen = false;

  categories = [
    { id: 'all', name: 'All Items' },
    { id: 'starters', name: 'Starters' },
    { id: 'mains', name: 'Main Courses' },
    { id: 'desserts', name: 'Desserts' },
    { id: 'beverages', name: 'Beverages' },
  ];

  dietaryFilters = [
    { id: 'all', name: 'All' },
    { id: 'vegan', name: 'Vegan' },
    { id: 'vegetarian', name: 'Vegetarian' },
    { id: 'gluten-free', name: 'Gluten Free' },
  ];

  menuItems: MenuItem[] = [
    {
      id: 1,
      name: 'Bruschetta Classica',
      description: 'Toasted bread with fresh tomatoes, basil, and garlic',
      price: 12,
      image: '/placeholder.svg?height=200&width=300',
      category: 'starters',
      ingredients: ['Bread', 'Tomatoes', 'Basil', 'Garlic', 'Olive Oil'],
      allergens: ['Gluten'],
      dietary: ['vegetarian'],
    },
    {
      id: 2,
      name: 'Spaghetti Carbonara',
      description: 'Classic Roman pasta with eggs, cheese, and pancetta',
      price: 18,
      image: '/placeholder.svg?height=200&width=300',
      category: 'mains',
      ingredients: [
        'Spaghetti',
        'Eggs',
        'Pecorino Romano',
        'Pancetta',
        'Black Pepper',
      ],
      allergens: ['Gluten', 'Eggs', 'Dairy'],
      dietary: [],
    },
    {
      id: 3,
      name: 'Margherita Pizza',
      description:
        'Traditional pizza with tomato sauce, mozzarella, and fresh basil',
      price: 16,
      image: '/placeholder.svg?height=200&width=300',
      category: 'mains',
      ingredients: [
        'Pizza Dough',
        'Tomato Sauce',
        'Mozzarella',
        'Basil',
        'Olive Oil',
      ],
      allergens: ['Gluten', 'Dairy'],
      dietary: ['vegetarian'],
    },
    {
      id: 4,
      name: 'Tiramisu',
      description:
        'Classic Italian dessert with coffee-soaked ladyfingers and mascarpone',
      price: 9,
      image: '/placeholder.svg?height=200&width=300',
      category: 'desserts',
      ingredients: [
        'Ladyfingers',
        'Coffee',
        'Mascarpone',
        'Eggs',
        'Cocoa Powder',
      ],
      allergens: ['Gluten', 'Eggs', 'Dairy'],
      dietary: ['vegetarian'],
    },
    {
      id: 5,
      name: 'Vegan Arancini',
      description: 'Crispy rice balls with mushroom and herb filling',
      price: 14,
      image: '/placeholder.svg?height=200&width=300',
      category: 'starters',
      ingredients: ['Arborio Rice', 'Mushrooms', 'Herbs', 'Nutritional Yeast'],
      allergens: [],
      dietary: ['vegan', 'gluten-free'],
    },
    {
      id: 6,
      name: 'Italian Wine Selection',
      description: 'Curated selection of Italian red and white wines',
      price: 8,
      image: '/placeholder.svg?height=200&width=300',
      category: 'beverages',
      ingredients: ['Grapes'],
      allergens: ['Sulfites'],
      dietary: ['vegan'],
    },
  ];

  get filteredItems(): MenuItem[] {
    return this.menuItems.filter((item) => {
      const categoryMatch =
        this.selectedCategory === 'all' ||
        item.category === this.selectedCategory;
      const dietaryMatch =
        this.selectedDietaryFilter === 'all' ||
        item.dietary.includes(this.selectedDietaryFilter);
      return categoryMatch && dietaryMatch;
    });
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  selectDietaryFilter(filter: string) {
    this.selectedDietaryFilter = filter;
  }

  openModal(item: MenuItem) {
    this.selectedItem = item;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedItem = null;
  }
}

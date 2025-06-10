import { Component, inject, type OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { Product } from '../../models/product/product.model';
import type { Category } from '../../models/product/category.model';
import type { Ingredient } from '../../models/product/ingredient.model';
import { ProductManagementService } from '../../services/product-management.service';

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css'],
})
export class ProductManagementComponent implements OnInit {
  // Modal states
  showProductModal = false;
  showCategoryModal = false;
  showIngredientModal = false;
  showDeleteConfirmModal = false;

  // Edit mode flags
  isEditingProduct = false;
  isEditingCategory = false;
  isEditingIngredient = false;

  // Current editing IDs
  editingProductId?: number;
  editingCategoryId?: number;
  editingIngredientId?: number;

  // Delete confirmation
  deleteItem: {
    type: 'product' | 'category' | 'ingredient';
    id: number;
    name: string;
  } | null = null;

  // Form objects
  newProduct: Product = {
    name: '',
    description: '',
    price: 0,
    categoryId: 0,
    ingredientIds: [],
  };

  newCategory: Category = {
    name: '',
    description: '',
  };

  newIngredient: Ingredient = {
    name: '',
  };

  // Notification system
  notifications: Notification[] = [];
  private notificationIdCounter = 1;

  // Data arrays
  ingredients: Ingredient[] = [];
  categories: Category[] = [];
  products: Product[] = [];
  selectedIngredients: number[] = [];

  private productService = inject(ProductManagementService);

  ngOnInit(): void {
    this.loadIngredients();
    this.loadCategories();
    this.loadProducts();
  }

  // Notification Methods
  showNotification(
    message: string,
    type: 'success' | 'error' | 'warning' | 'info' = 'success'
  ) {
    const notification: Notification = {
      id: this.notificationIdCounter++,
      message,
      type,
    };

    this.notifications.push(notification);

    // Auto remove after 2 seconds
    setTimeout(() => {
      this.removeNotification(notification.id);
    }, 2000);
  }

  removeNotification(id: number) {
    this.notifications = this.notifications.filter((n) => n.id !== id);
  }

  // Delete Confirmation Methods
  confirmDelete(
    type: 'product' | 'category' | 'ingredient',
    id: number,
    name: string
  ) {
    this.deleteItem = { type, id, name };
    this.showDeleteConfirmModal = true;
  }

  cancelDelete() {
    this.deleteItem = null;
    this.showDeleteConfirmModal = false;
  }

  executeDelete() {
    if (!this.deleteItem) return;

    const { type, id, name } = this.deleteItem;

    switch (type) {
      case 'product':
        this.productService.deleteProduct(id).subscribe({
          next: () => {
            this.products = this.products.filter((p) => p.id !== id);
            this.showNotification(`Product "${name}" deleted successfully!`);
          },
          error: (err) => {
            console.error('Error deleting product', err);
            this.showNotification('Failed to delete product', 'error');
          },
        });
        break;
      case 'category':
        this.productService.deleteCategory(id).subscribe({
          next: () => {
            this.categories = this.categories.filter((c) => c.id !== id);
            this.showNotification(`Category "${name}" deleted successfully!`);
          },
          error: (err) => {
            console.error('Error deleting category', err);
            this.showNotification('Failed to delete category', 'error');
          },
        });
        break;
      case 'ingredient':
        this.productService.deleteIngredient(id).subscribe({
          next: () => {
            this.ingredients = this.ingredients.filter((i) => i.id !== id);
            this.showNotification(`Ingredient "${name}" deleted successfully!`);
          },
          error: (err) => {
            console.error('Error deleting ingredient', err);
            this.showNotification('Failed to delete ingredient', 'error');
          },
        });
        break;
    }

    this.cancelDelete();
  }

  // Ingredient Methods
  loadIngredients() {
    this.productService.getIngredients().subscribe({
      next: (data) => {
        this.ingredients = data;
      },
      error: (err) => {
        console.error('Error loading ingredients', err);
        this.showNotification('Failed to load ingredients', 'error');
      },
    });
  }

  openIngredientModal(ingredient?: Ingredient) {
    this.showIngredientModal = true;
    if (ingredient) {
      this.isEditingIngredient = true;
      this.editingIngredientId = ingredient.id;
      this.newIngredient = { ...ingredient };
    } else {
      this.resetIngredientForm();
    }
  }

  closeIngredientModal() {
    this.showIngredientModal = false;
    this.resetIngredientForm();
  }

  resetIngredientForm() {
    this.newIngredient = { name: '' };
    this.isEditingIngredient = false;
    this.editingIngredientId = undefined;
  }

  saveIngredient() {
    if (this.newIngredient.name) {
      if (this.isEditingIngredient && this.editingIngredientId) {
        // Update existing ingredient
        this.productService
          .updateIngredient(this.editingIngredientId, this.newIngredient)
          .subscribe({
            next: (updated) => {
              const index = this.ingredients.findIndex(
                (i) => i.id === this.editingIngredientId
              );
              if (index !== -1) {
                this.ingredients[index] = updated;
              }
              this.closeIngredientModal();
              this.showNotification('Ingredient updated successfully!');
            },
            error: (err) => {
              console.error('Error updating ingredient', err);
              this.showNotification('Failed to update ingredient', 'error');
            },
          });
      } else {
        // Create new ingredient
        this.productService.addIngredient(this.newIngredient).subscribe({
          next: (created) => {
            this.ingredients.push(created);
            this.closeIngredientModal();
            this.showNotification('Ingredient added successfully!');
          },
          error: (err) => {
            console.error('Error saving ingredient', err);
            this.showNotification('Failed to save ingredient', 'error');
          },
        });
      }
    }
  }

  // Category Methods
  loadCategories() {
    this.productService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Error loading categories', err);
        this.showNotification('Failed to load categories', 'error');
      },
    });
  }

  openCategoryModal(category?: Category) {
    this.showCategoryModal = true;
    if (category) {
      this.isEditingCategory = true;
      this.editingCategoryId = category.id;
      this.newCategory = { ...category };
    } else {
      this.resetCategoryForm();
    }
  }

  closeCategoryModal() {
    this.showCategoryModal = false;
    this.resetCategoryForm();
  }

  resetCategoryForm() {
    this.newCategory = { name: '', description: '' };
    this.isEditingCategory = false;
    this.editingCategoryId = undefined;
  }

  saveCategory() {
    if (this.newCategory.name) {
      if (this.isEditingCategory && this.editingCategoryId) {
        // Update existing category
        this.productService
          .updateCategory(this.editingCategoryId, this.newCategory)
          .subscribe({
            next: (updated) => {
              const index = this.categories.findIndex(
                (c) => c.id === this.editingCategoryId
              );
              if (index !== -1) {
                this.categories[index] = updated;
              }
              this.closeCategoryModal();
              this.showNotification('Category updated successfully!');
            },
            error: (err) => {
              console.error('Error updating category', err);
              this.showNotification('Failed to update category', 'error');
            },
          });
      } else {
        // Create new category
        this.productService.addCategory(this.newCategory).subscribe({
          next: (created) => {
            this.categories.push(created);
            this.closeCategoryModal();
            this.showNotification('Category added successfully!');
          },
          error: (err) => {
            console.error('Error saving category', err);
            this.showNotification('Failed to save category', 'error');
          },
        });
      }
    }
  }

  // Product Methods
  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
      },
      error: (err) => {
        console.error('Error loading products', err);
        this.showNotification('Failed to load products', 'error');
      },
    });
  }

  openProductModal(product?: Product) {
    this.showProductModal = true;
    if (product) {
      this.isEditingProduct = true;
      this.editingProductId = product.id;
      this.newProduct = { ...product };
      this.selectedIngredients = [...product.ingredientIds];
    } else {
      this.resetProductForm();
    }
  }

  closeProductModal() {
    this.showProductModal = false;
    this.resetProductForm();
  }

  resetProductForm() {
    this.newProduct = {
      name: '',
      description: '',
      price: 0,
      categoryId: 0,
      ingredientIds: [],
    };
    this.selectedIngredients = [];
    this.isEditingProduct = false;
    this.editingProductId = undefined;
  }

  saveProduct() {
    if (
      this.newProduct.name &&
      this.newProduct.price > 0 &&
      this.newProduct.categoryId > 0
    ) {
      const product: Product = {
        ...this.newProduct,
        ingredientIds: [...this.selectedIngredients],
      };

      if (this.isEditingProduct && this.editingProductId) {
        // Update existing product
        this.productService
          .updateProduct(this.editingProductId, product)
          .subscribe({
            next: (updated) => {
              const index = this.products.findIndex(
                (p) => p.id === this.editingProductId
              );
              if (index !== -1) {
                this.products[index] = updated;
              }
              this.closeProductModal();
              this.showNotification('Product updated successfully!');
            },
            error: (err) => {
              console.error('Error updating product', err);
              this.showNotification('Failed to update product', 'error');
            },
          });
      } else {
        // Create new product
        this.productService.addProduct(product).subscribe({
          next: (created) => {
            this.products.push(created);
            this.closeProductModal();
            this.showNotification('Product added successfully!');
          },
          error: (err) => {
            console.error('Error saving product', err);
            this.showNotification('Failed to save product', 'error');
          },
        });
      }
    }
  }

  // Helper Methods
  toggleIngredient(ingredientId: number) {
    const index = this.selectedIngredients.indexOf(ingredientId);
    if (index > -1) {
      this.selectedIngredients.splice(index, 1);
    } else {
      this.selectedIngredients.push(ingredientId);
    }
    this.newProduct.ingredientIds = [...this.selectedIngredients];
  }

  isIngredientSelected(ingredientId: number): boolean {
    return this.selectedIngredients.includes(ingredientId);
  }

  getCategoryName(categoryId: number): string {
    const category = this.categories.find((cat) => cat.id === categoryId);
    return category ? category.name : 'Unknown';
  }

  getIngredientNames(ingredientIds: number[]): string {
    const names = ingredientIds.map((id) => {
      const ingredient = this.ingredients.find((ing) => ing.id === id);
      return ingredient ? ingredient.name : 'Unknown';
    });
    return names.join(', ');
  }

  getModalTitle(type: 'product' | 'category' | 'ingredient'): string {
    switch (type) {
      case 'product':
        return this.isEditingProduct ? 'Edit Product' : 'Add New Product';
      case 'category':
        return this.isEditingCategory ? 'Edit Category' : 'Add New Category';
      case 'ingredient':
        return this.isEditingIngredient
          ? 'Edit Ingredient'
          : 'Add New Ingredient';
    }
  }

  getSaveButtonText(type: 'product' | 'category' | 'ingredient'): string {
    switch (type) {
      case 'product':
        return this.isEditingProduct ? 'Update Product' : 'Save Product';
      case 'category':
        return this.isEditingCategory ? 'Update Category' : 'Save Category';
      case 'ingredient':
        return this.isEditingIngredient
          ? 'Update Ingredient'
          : 'Save Ingredient';
    }
  }
}

import { Component, inject, type OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  type FormGroup,
  type FormArray,
  Validators,
} from '@angular/forms';
import { OrderService } from '../../services/order.service';
import { ProductManagementService } from '../../services/product-management.service';
import type {
  OrderDTO,
  CreateOrderRequest,
  OrderItemDTO,
} from '../../models/order/order.model';
import type { Product } from '../../models/product/product.model';

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

interface OrderFilter {
  searchTerm: string;
  dateFrom: string;
  dateTo: string;
  minAmount: number | null;
  maxAmount: number | null;
  sortBy: 'orderDate' | 'totalAmount' | 'customerName';
  sortDirection: 'asc' | 'desc';
}

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css'],
})
export class OrderManagementComponent implements OnInit {
  // ... (proprietățile componentei rămân la fel) ...
  showOrderModal = false;
  showDeleteConfirmModal = false;
  showOrderDetailsModal = false;
  isEditingOrder = false;
  editingOrderId?: number;
  isLoading = false;
  isSaving = false;
  isLoadingProducts = false;
  orders: OrderDTO[] = [];
  products: Product[] = [];
  filteredOrders: OrderDTO[] = [];
  selectedOrder?: OrderDTO;
  orderForm: FormGroup;
  filterForm: FormGroup;
  currentPage = 1;
  itemsPerPage = 10;
  totalItems = 0;
  filters: OrderFilter = {
    searchTerm: '',
    dateFrom: '',
    dateTo: '',
    minAmount: null,
    maxAmount: null,
    sortBy: 'orderDate',
    sortDirection: 'desc',
  };
  activeFilters: string[] = [];
  timeFilter: 'all' | 'past' | 'today' | 'future' = 'today';
  statusFilter: 'all' | 'pending' | 'confirmed' | 'delivered' | 'cancelled' =
    'all';
  deleteOrder: { id: number; customerName: string } | null = null;
  notifications: Notification[] = [];
  private notificationIdCounter = 1;
  private orderService = inject(OrderService);
  private productService = inject(ProductManagementService);
  private fb = inject(FormBuilder);

  constructor() {
    this.orderForm = this.createOrderForm();
    this.filterForm = this.createFilterForm();
  }

  ngOnInit(): void {
    this.timeFilter = 'today';
    this.loadOrders();
    this.loadProducts();
    this.setupFilterSubscription();
  }

  // Am eliminat funcția parseBackendDate - nu mai este necesară!

  private createOrderForm(): FormGroup {
    return this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(2)]],
      customerAddress: ['', [Validators.required, Validators.minLength(5)]],
      orderItems: this.fb.array([]),
    });
  }

  private createFilterForm(): FormGroup {
    return this.fb.group({
      searchTerm: [''],
      dateFrom: [''],
      dateTo: [''],
      minAmount: [null],
      maxAmount: [null],
      sortBy: ['orderDate'],
      sortDirection: ['desc'],
    });
  }

  private createOrderItemForm(item?: OrderItemDTO): FormGroup {
    return this.fb.group({
      productId: [item?.productId || '', Validators.required],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
      priceAtOrder: [item?.priceAtOrder || 0],
    });
  }

  get orderItems(): FormArray {
    return this.orderForm.get('orderItems') as FormArray;
  }

  get paginatedOrders(): OrderDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredOrders.slice(startIndex, endIndex);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredOrders.length / this.itemsPerPage);
  }

  // în fișierul order-management.component.ts

  // în fișierul order-management.component.ts

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (orders) => {
        // Atribuire directă, fără .map()
        this.orders = orders;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.showNotification('Failed to load orders', 'error');
        this.isLoading = false;
      },
    });
  }

  loadProducts(): void {
    // ... (această funcție rămâne la fel)
    this.isLoadingProducts = true;
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.isLoadingProducts = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.showNotification('Failed to load products', 'error');
        this.isLoadingProducts = false;
      },
    });
  }

  private setupFilterSubscription(): void {
    this.filterForm.valueChanges.subscribe((filterValues) => {
      this.filters = { ...this.filters, ...filterValues };
      this.applyFilters();
    });
  }

  // în fișierul order-management.component.ts

  applyFilters(): void {
    // 1. Începem cu lista completă de comenzi
    let filtered = [...this.orders];
    this.activeFilters = [];

    // --- Filtrele rapide de timp (Past, Today, Future) ---
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    switch (this.timeFilter) {
      case 'past':
        filtered = filtered.filter((order) => {
          // Asigurăm că orderDate există înainte de a compara
          return order.orderDate && order.orderDate < today;
        });
        this.activeFilters.push('Past Orders');
        break;
      case 'today':
        filtered = filtered.filter((order) => {
          // O comandă este de "azi" dacă data sa este >= începutul zilei de azi ȘI < începutul zilei de mâine
          return (
            order.orderDate &&
            order.orderDate >= today &&
            order.orderDate < tomorrow
          );
        });
        this.activeFilters.push("Today's Orders");
        break;
      case 'future':
        filtered = filtered.filter((order) => {
          return order.orderDate && order.orderDate >= tomorrow;
        });
        this.activeFilters.push('Future Orders');
        break;
    }

    // --- Filtrul de căutare (Search Term) ---
    if (this.filters.searchTerm) {
      const searchTerm = this.filters.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (order) =>
          order.customerName.toLowerCase().includes(searchTerm) ||
          order.customerAddress.toLowerCase().includes(searchTerm) ||
          order.id?.toString().includes(searchTerm)
      );
      this.activeFilters.push(`Search: "${this.filters.searchTerm}"`);
    }

    // --- [LOGICĂ NOUĂ] Filtrul pentru interval de date custom (când timeFilter este 'all') ---
    if (this.timeFilter === 'all') {
      if (this.filters.dateFrom) {
        const fromDate = new Date(this.filters.dateFrom);
        fromDate.setHours(0, 0, 0, 0); // Comparăm de la începutul zilei
        filtered = filtered.filter(
          (order) => order.orderDate && order.orderDate >= fromDate
        );
        this.activeFilters.push(`From: ${this.filters.dateFrom}`);
      }
      if (this.filters.dateTo) {
        const toDate = new Date(this.filters.dateTo);
        toDate.setHours(23, 59, 59, 999); // Comparăm până la sfârșitul zilei
        filtered = filtered.filter(
          (order) => order.orderDate && order.orderDate <= toDate
        );
        this.activeFilters.push(`To: ${this.filters.dateTo}`);
      }
    }

    // --- [LOGICĂ NOUĂ] Filtrul pentru interval de preț (Amount) ---
    if (this.filters.minAmount != null && this.filters.minAmount >= 0) {
      filtered = filtered.filter(
        (order) => (order.totalAmount || 0) >= this.filters.minAmount!
      );
      this.activeFilters.push(
        `Min Amount: ${this.formatCurrency(this.filters.minAmount)}`
      );
    }
    if (this.filters.maxAmount != null && this.filters.maxAmount >= 0) {
      filtered = filtered.filter(
        (order) => (order.totalAmount || 0) <= this.filters.maxAmount!
      );
      this.activeFilters.push(
        `Max Amount: ${this.formatCurrency(this.filters.maxAmount)}`
      );
    }

    // --- Logica de sortare (îmbunătățită) ---
    filtered.sort((a, b) => {
      const direction = this.filters.sortDirection === 'asc' ? 1 : -1;
      switch (this.filters.sortBy) {
        case 'orderDate':
          // Comparație directă a timpului pentru obiecte Date
          const timeA = a.orderDate ? a.orderDate.getTime() : 0;
          const timeB = b.orderDate ? b.orderDate.getTime() : 0;
          return (timeA - timeB) * direction;
        case 'totalAmount':
          return ((a.totalAmount || 0) - (b.totalAmount || 0)) * direction;
        case 'customerName':
          return a.customerName.localeCompare(b.customerName) * direction;
        default:
          return 0;
      }
    });

    // 2. Actualizăm lista filtrată și paginarea
    this.filteredOrders = filtered;
    this.totalItems = filtered.length;
    this.currentPage = 1; // Resetăm la prima pagină după fiecare filtrare
  }

  // ... (restul funcțiilor până la saveOrder)
  addOrderItem() {
    this.orderItems.push(this.createOrderItemForm());
  }
  removeOrderItem(index: number) {
    this.orderItems.removeAt(index);
    this.calculateTotal();
  }
  onProductChange(index: number) {
    const productId = this.orderItems.at(index).get('productId')?.value;
    const product = this.products.find((p) => p.id === Number(productId));
    if (product) {
      this.orderItems.at(index).patchValue({ priceAtOrder: product.price });
      this.calculateTotal();
    }
  }
  onQuantityChange() {
    this.calculateTotal();
  }
  calculateTotal() {
    let total = 0;
    this.orderItems.controls.forEach((control) => {
      const quantity = control.get('quantity')?.value || 0;
      const price = control.get('priceAtOrder')?.value || 0;
      total += quantity * price;
    });
    return total;
  }
  openOrderModal(order?: OrderDTO) {
    this.showOrderModal = true;
    if (order) {
      this.isEditingOrder = true;
      this.editingOrderId = order.id;
      this.populateOrderForm(order);
    } else {
      this.resetOrderForm();
      this.addOrderItem();
    }
  }
  closeOrderModal() {
    this.showOrderModal = false;
    this.resetOrderForm();
  }
  openOrderDetailsModal(order: OrderDTO) {
    this.selectedOrder = order;
    this.showOrderDetailsModal = true;
  }
  closeOrderDetailsModal() {
    this.selectedOrder = undefined;
    this.showOrderDetailsModal = false;
  }
  private populateOrderForm(order: OrderDTO) {
    this.orderForm.patchValue({
      customerName: order.customerName,
      customerAddress: order.customerAddress,
    });
    while (this.orderItems.length !== 0) {
      this.orderItems.removeAt(0);
    }
    if (order.orderItems) {
      order.orderItems.forEach((item) => {
        this.orderItems.push(this.createOrderItemForm(item));
      });
    }
  }
  private resetOrderForm() {
    this.orderForm.reset();
    this.isEditingOrder = false;
    this.editingOrderId = undefined;
    while (this.orderItems.length !== 0) {
      this.orderItems.removeAt(0);
    }
  }

  saveOrder(): void {
    // Logica de salvare este acum corectă, deoarece ne bazăm pe `loadOrders`
    if (!this.orderForm.valid) {
      this.showNotification(
        'Please fill in all required fields correctly',
        'error'
      );
      return;
    }
    this.isSaving = true;
    const formValue = this.orderForm.value;

    if (this.isEditingOrder && this.editingOrderId) {
      const orderDTO: OrderDTO = {
        id: this.editingOrderId,
        customerName: formValue.customerName,
        customerAddress: formValue.customerAddress,
        orderItems: formValue.orderItems,
      };
      this.orderService.updateOrder(this.editingOrderId, orderDTO).subscribe({
        next: () => {
          this.loadOrders();
          this.closeOrderModal();
          this.showNotification('Order updated successfully!');
          this.isSaving = false;
        },
        error: (error) => {
          console.error('Error updating order:', error);
          this.showNotification('Failed to update order', 'error');
          this.isSaving = false;
        },
      });
    } else {
      const createRequest: CreateOrderRequest = {
        customerName: formValue.customerName,
        customerAddress: formValue.customerAddress,
        items: formValue.orderItems.map((item: any) => ({
          productId: Number(item.productId),
          quantity: item.quantity,
        })),
      };
      this.orderService.createOrder(createRequest).subscribe({
        next: () => {
          this.loadOrders();
          this.closeOrderModal();
          this.showNotification('Order created successfully!');
          this.isSaving = false;
        },
        error: (error) => {
          console.error('Error creating order:', error);
          this.showNotification('Failed to create order', 'error');
          this.isSaving = false;
        },
      });
    }
  }

  // ... (restul funcțiilor până la formatDate)
  confirmDeleteOrder(order: OrderDTO) {
    this.deleteOrder = { id: order.id!, customerName: order.customerName };
    this.showDeleteConfirmModal = true;
  }
  cancelDelete() {
    this.deleteOrder = null;
    this.showDeleteConfirmModal = false;
  }
  executeDelete() {
    if (!this.deleteOrder) return;
    this.orderService.deleteOrder(this.deleteOrder.id).subscribe({
      next: () => {
        this.orders = this.orders.filter((o) => o.id !== this.deleteOrder!.id);
        this.applyFilters();
        this.showNotification(
          `Order for ${this.deleteOrder!.customerName} deleted successfully!`
        );
        this.cancelDelete();
      },
      error: (error) => {
        console.error('Error deleting order:', error);
        this.showNotification('Failed to delete order', 'error');
        this.cancelDelete();
      },
    });
  }
  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
  getProductName(productId: number) {
    const product = this.products.find((p) => p.id === productId);
    return product ? product.name : 'Unknown Product';
  }

  formatDate(dateInput: any): string {
    // Acum 'dateInput' ar trebui să fie un obiect Date valid.
    if (dateInput instanceof Date && !isNaN(dateInput.getTime())) {
      return dateInput.toLocaleDateString();
    }
    return 'Invalid Date';
  }

  // ... (restul fișierului rămâne la fel)
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  }
  clearFilters() {
    this.filterForm.reset({
      searchTerm: '',
      dateFrom: '',
      dateTo: '',
      minAmount: null,
      maxAmount: null,
      sortBy: 'orderDate',
      sortDirection: 'desc',
    });
  }
  exportOrders() {
    this.showNotification('Export functionality coming soon!', 'info');
  }
  setTimeFilter(filter: 'all' | 'past' | 'today' | 'future') {
    this.timeFilter = filter;
    if (filter !== 'all') {
      this.filterForm.patchValue({ dateFrom: '', dateTo: '' });
    }
    this.applyFilters();
  }
  clearAllFilters() {
    this.timeFilter = 'all';
    this.statusFilter = 'all';
    this.filterForm.reset({
      searchTerm: '',
      dateFrom: '',
      dateTo: '',
      minAmount: null,
      maxAmount: null,
      sortBy: 'orderDate',
      sortDirection: 'desc',
    });
    this.applyFilters();
  }
  getActiveFilterCount(): number {
    return this.activeFilters.length;
  }
  mathMin(a: number, b: number): number {
    return Math.min(a, b);
  }
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
    setTimeout(() => {
      this.removeNotification(notification.id);
    }, 5000);
  }
  removeNotification(id: number) {
    this.notifications = this.notifications.filter((n) => n.id !== id);
  }
}

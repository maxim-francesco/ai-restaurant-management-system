import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { type Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import type { OrderDTO, CreateOrderRequest } from '../models/order/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl =
    'http://product-service-production-991d.up.railway.app:8080/api/orders';

  private httpHeaders = new HttpHeaders({
    'Content-Type': 'application/json',
  });

  private http = inject(HttpClient);

  // în fișierul order.service.ts

  // în fișierul order.service.ts

  private dateReviver = (key: string, value: any): any => {
    // Verificăm dacă cheia este 'orderDate' și valoarea este un array
    if (key === 'orderDate' && Array.isArray(value) && value.length >= 6) {
      // ATENȚIE: Constructorul Date din JavaScript folosește luna 0-indexată (0=Ianuarie, 1=Februarie, etc.)
      // De aceea scădem 1 din valoarea lunii primite de la backend (value[1]).
      // new Date(an, luna, zi, ora, minut, secunda)
      return new Date(
        value[0],
        value[1] - 1,
        value[2],
        value[3],
        value[4],
        value[5]
      );
    }

    // Pentru toate celelalte valori, le returnăm neschimbate
    return value;
  };

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(
      () => new Error('Something bad happened; please try again later.')
    );
  }

  createOrder(orderRequest: CreateOrderRequest): Observable<OrderDTO> {
    return this.http
      .post<OrderDTO>(this.apiUrl, orderRequest, { headers: this.httpHeaders })
      .pipe(catchError(this.handleError));
  }

  getOrderById(id: number): Observable<OrderDTO> {
    const url = `${this.apiUrl}/${id}`;
    // Cerem răspunsul ca text pentru a preveni auto-parsarea
    return this.http.get(url, { responseType: 'text' }).pipe(
      // Parsăm manual textul JSON folosind funcția noastră "reviver"
      map((jsonString) => JSON.parse(jsonString, this.dateReviver)),
      catchError(this.handleError)
    );
  }

  getAllOrders(): Observable<OrderDTO[]> {
    // Cerem răspunsul ca text pentru a preveni auto-parsarea
    return this.http.get(this.apiUrl, { responseType: 'text' }).pipe(
      // Parsăm manual textul JSON folosind funcția noastră "reviver"
      map((jsonString) => JSON.parse(jsonString, this.dateReviver)),
      catchError(this.handleError)
    );
  }

  updateOrder(id: number, orderDTO: OrderDTO): Observable<OrderDTO> {
    const url = `${this.apiUrl}/${id}`;
    return this.http
      .put<OrderDTO>(url, orderDTO, { headers: this.httpHeaders })
      .pipe(catchError(this.handleError));
  }

  deleteOrder(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url).pipe(catchError(this.handleError));
  }
}

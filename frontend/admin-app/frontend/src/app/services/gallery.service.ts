// src/app/services/gallery.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventCategory } from '../models/gallery/event-category.model';
import { Event } from '../models/gallery/event.model';

@Injectable({
  providedIn: 'root',
})
export class GalleryService {
  // URL-ul de bază al microserviciului tău Spring Boot
  private readonly apiUrl =
    'https://gallery-service-production-9dea.up.railway.app/api/v1';

  constructor(private http: HttpClient) {}

  // === Metode pentru Categorii ===

  getAllCategories(): Observable<EventCategory[]> {
    return this.http.get<EventCategory[]>(`${this.apiUrl}/categories`);
  }

  createCategory(categoryData: { name: string }): Observable<EventCategory> {
    return this.http.post<EventCategory>(
      `${this.apiUrl}/categories`,
      categoryData
    );
  }

  deleteCategory(categoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/categories/${categoryId}`);
  }

  // === Metode pentru Evenimente ===

  addEvent(
    categoryId: number,
    eventName: string,
    photo: File
  ): Observable<Event> {
    const formData = new FormData();

    // Cheile de aici ('categoryId', 'eventData', 'photo') trebuie să corespundă
    // exact cu numele @RequestParam din controller-ul Spring!
    formData.append('categoryId', categoryId.toString());
    formData.append('eventData', JSON.stringify({ name: eventName }));
    formData.append('photo', photo);

    return this.http.post<Event>(`${this.apiUrl}/events`, formData);
  }

  deleteEvent(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/events/${eventId}`);
  }

  // Helper pentru a construi URL-ul complet al imaginii
  getImageUrl(photoFileName: string): string {
    return `https://gallery-service-production-9dea.up.railway.app/uploads/gallery/${photoFileName}`;
  }
}

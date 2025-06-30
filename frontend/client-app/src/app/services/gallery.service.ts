// src/app/services/gallery.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventCategory } from '../models/gallery/event-category.model';

@Injectable({
  providedIn: 'root',
})
export class GalleryService {
  // URL-ul de bază al microserviciului tău de galerie.
  // Asigură-te că portul 8086 este corect.
  private readonly apiUrl =
    'http://gallery-service-production-9dea.up.railway.app:8080/api/v1';

  constructor(private http: HttpClient) {}

  /**
   * Prelucrează toate categoriile de evenimente, inclusiv pozele asociate.
   * Acesta este singurul apel necesar pentru a afișa întreaga galerie.
   * @returns Un Observable care emite un array de categorii de evenimente.
   */
  public getAllCategoriesWithEvents(): Observable<EventCategory[]> {
    return this.http.get<EventCategory[]>(`${this.apiUrl}/categories`);
  }

  /**
   * Metodă ajutătoare pentru a construi URL-ul complet al unei imagini.
   * Va fi folosită în template-urile componentelor pentru a afișa pozele.
   * @param photoFileName Numele fișierului pozei (ex: "some-uuid-123.jpg")
   * @returns URL-ul absolut către imagine.
   */
  public getFullImageUrl(photoFileName: string): string {
    // Asigură-te că URL-ul și calea "/uploads/gallery/" corespund cu configurația
    // din WebConfig.java din proiectul Spring Boot.
    return `http://localhost:8086/uploads/gallery/${photoFileName}`;
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Interfață pentru a defini structura datelor unei rezervări.
 * Asigură o verificare a tipurilor de date (type-safety).
 */
export interface ReservationData {
  customerName: string;
  phoneNumber: string;
  reservationDateTime: string; // Format: "YYYY-MM-DDTHH:MM:SS"
  numberOfPeople: number;
  // Poți adăuga și alte câmpuri aici dacă este necesar
}

@Injectable({
  providedIn: 'root',
})
export class ReservationN8nService {
  // URL-ul pentru webhook-ul n8n
  private n8nWebhookUrl =
    'https://francescoomaxim.app.n8n.cloud/webhook-test/e6ff4ac1-cfed-4e91-aee0-389c801aefd6';

  constructor(private http: HttpClient) {}

  /**
   * Declanșează workflow-ul n8n pentru a crea o rezervare.
   * @param reservationData Obiectul cu datele rezervării de trimis.
   * @returns Un Observable cu răspunsul de la serverul n8n.
   */
  public triggerReservationWorkflow(
    reservationData: ReservationData
  ): Observable<any> {
    // Corpul cererii va conține datele rezervării.
    const body = reservationData;

    console.log(
      'Se trimit datele rezervării către n8n (fără autentificare)...',
      body
    );

    // Se face cererea POST către webhook-ul n8n.
    // Am eliminat complet parametrul 'headers'. HttpClient va seta automat
    // 'Content-Type: application/json' deoarece trimitem un obiect.
    return this.http.post<any>(this.n8nWebhookUrl, body);
  }
}

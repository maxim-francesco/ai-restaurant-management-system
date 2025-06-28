import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class N8nIntegrationService {
  // Înlocuiește cu URL-ul tău de production de la n8n
  private n8nWebhookUrl =
    'https://francescoomaxim.app.n8n.cloud/webhook-test/ea8dd505-0bae-437b-a80a-8220807934fa';

  // Înlocuiește cu cheia secretă pe care ai definit-o în n8n
  private apiKey = 'super-secret-key-123456789';

  constructor(private http: HttpClient) {}

  public triggerWorkflow(): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // Aici pui header-ul de autentificare definit în n8n
      'X-N8N-API-KEY': this.apiKey,
    });

    // Corpul cererii poate fi gol dacă nu trebuie să trimiți date
    const body = {};

    console.log('Declanșare workflow n8n...');
    return this.http.post<any>(this.n8nWebhookUrl, body, { headers: headers });
  }
}

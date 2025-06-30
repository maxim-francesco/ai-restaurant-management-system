import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContactInfo } from '../models/contact/contact-info.model';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
  private apiUrl =
    'http://contact-service-production.up.railway.app:8080/api/contact';

  constructor(private http: HttpClient) {}

  getInfo(): Observable<ContactInfo> {
    return this.http.get<ContactInfo>(this.apiUrl);
  }
}

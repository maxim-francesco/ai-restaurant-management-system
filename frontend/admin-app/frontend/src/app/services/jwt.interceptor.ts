import { Injectable, inject } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private authService = inject(AuthService);

  // --- Adresele API pe care vrem să le securizăm ---
  private reservationsApiUrl =
    'https://reservation-service-production.up.railway.app/api/reservations'; // Schimbă portul dacă e necesar
  private restaurantApiUrl =
    'https://product-service-production-991d.up.railway.app/api/'; // Schimbă portul dacă e necesar

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    // --- MODIFICARE CHEIE: Am adăugat a doua condiție în 'if' ---
    // Verificăm dacă request-ul este pentru unul dintre API-urile noastre interne
    const isApiRequest =
      request.url.startsWith(this.reservationsApiUrl) ||
      request.url.startsWith(this.restaurantApiUrl);

    if (token && isApiRequest) {
      // Dacă avem token ȘI este un request către API-urile noastre, adăugăm header-ul
      const clonedRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(
        'Interceptor: Adding auth header to API request',
        clonedRequest
      );
      return next.handle(clonedRequest);
    }

    // Pentru orice alt request, îl lăsăm nemodificat
    return next.handle(request);
  }
}

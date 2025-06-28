import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

// Definim cheia aici, pentru a fi siguri că e aceeași cu cea din AuthService.
// Este un mic compromis de arhitectură pentru a garanta funcționarea.
const TOKEN_KEY = 'jwt_token';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Injectăm doar PLATFORM_ID pentru a verifica dacă suntem în browser.
  // Aceasta este o dependență 100% sigură care nu cauzează cicluri.
  const platformId = inject(PLATFORM_ID);

  // Verificăm dacă rulăm într-un mediu de browser, deoarece localStorage nu există pe server (SSR).
  if (isPlatformBrowser(platformId)) {
    // Citim token-ul direct din localStorage.
    // FĂRĂ AuthService, FĂRĂ Injector. Nicio dependență periculoasă.
    const authToken = localStorage.getItem(TOKEN_KEY);

    if (authToken) {
      // Dacă găsim token-ul, îl adăugăm la header.
      const clonedReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authToken}`),
      });
      return next(clonedReq);
    }
  }

  // Dacă nu suntem în browser sau nu există token, pasăm cererea originală mai departe.
  return next(req);
};

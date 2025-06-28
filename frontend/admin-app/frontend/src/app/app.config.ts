import {
  ApplicationConfig,
  APP_INITIALIZER,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration } from '@angular/platform-browser';
import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './services/auth.interceptor';
import { AuthService } from './services/auth.service';

// Funcția factory pentru APP_INITIALIZER
export function initializeAuth(authService: AuthService) {
  return () => authService.initAuthStatusCheck();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),

    // Provider-ul APP_INITIALIZER care rupe dependența circulară
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthService], // Specificăm dependențele pentru factory
      multi: true,
    },
  ],
};

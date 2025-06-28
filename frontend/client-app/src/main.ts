import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { AppComponent } from './app/app.component';
// 1. Importă provideHttpClient
import { provideHttpClient, withFetch } from '@angular/common/http';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([]),
    provideAnimationsAsync(),
    // 2. Adaugă provider-ul aici
    provideHttpClient(withFetch()),
  ],
}).catch((err) => console.error(err));

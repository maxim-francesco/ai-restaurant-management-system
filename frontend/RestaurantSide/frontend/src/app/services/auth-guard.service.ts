import { Injectable, inject } from '@angular/core';
import {
  type CanActivate,
  type ActivatedRouteSnapshot,
  type RouterStateSnapshot,
  Router,
  type UrlTree,
} from '@angular/router';
import type { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    // Check if the user is logged in
    if (this.authService.isLoggedIn()) {
      // Check if the route requires a specific role
      const requiredRole = route.data['requiredRole'] as string | undefined;

      if (requiredRole) {
        const userRole = this.authService.getCurrentUserRole();

        // If the route requires a specific role and the user doesn't have it
        if (userRole !== requiredRole) {
          // Redirect to an unauthorized page or dashboard
          return this.router.createUrlTree(['/unauthorized']);
        }
      }

      // User is logged in and has the required role (if any)
      return true;
    }

    // User is not logged in, redirect to login page
    // Don't store the attempted URL to avoid query parameters
    return this.router.createUrlTree(['/login']);
  }
}

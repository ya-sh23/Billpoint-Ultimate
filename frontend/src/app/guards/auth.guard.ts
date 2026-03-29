import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    const userRole = authService.getRole();
    const path = route.routeConfig?.path;

    // Optional: Role-based check
    if (path === 'admin-dashboard' && userRole !== 'ADMIN') {
      router.navigate(['/login']);
      return false;
    }
    if (path === 'shop-owner-dashboard' && userRole !== 'SHOP_OWNER') {
      router.navigate(['/login']);
      return false;
    }
    if (path === 'staff-dashboard' && userRole !== 'STAFF') {
      router.navigate(['/login']);
      return false;
    }
    if (path === 'customer-dashboard' && userRole !== 'CUSTOMER') {
      router.navigate(['/login']);
      return false;
    }

    return true;
  }

  // Not logged in
  router.navigate(['/login']);
  return false;
};

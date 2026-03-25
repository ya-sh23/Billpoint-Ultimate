import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials = {
    username: '',
    password: ''
  };

  selectedRole = 'CUSTOMER'; // Default role
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toast: ToastService
  ) {}

  selectRole(role: string) {
    this.selectedRole = role;
  }

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        const role = response.role;
        this.toast.success("Login successful!");
        
        switch(role) {
          case 'ADMIN':
            this.router.navigate(['/admin-dashboard']);
            break;
          case 'SHOP_OWNER':
            this.router.navigate(['/shop-owner-dashboard']);
            break;
          case 'STAFF':
            this.router.navigate(['/staff-dashboard']);
            break;
          case 'CUSTOMER':
            this.router.navigate(['/customer-dashboard']);
            break;
          default:
            this.toast.error('Invalid role detected');
        }
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err.error?.message || 'Login failed. Please check your credentials.';
        this.errorMessage = msg;
        this.toast.error(msg);
      }
    });
  }
}

import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  form = {
    username: '',
    email: '',
    password: '',
    phone: '',
    role: 'CUSTOMER',
    businessName: '',
    panCard: '',
    gstin: '',
    address: ''
  };

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toast: ToastService
  ) {}

  onSubmit() {

    this.authService.register(this.form).subscribe({

      next: (res:any) => {
        this.toast.success(res.message || "Registered successfully! Wait for approval.");
        this.router.navigate(['/login']);
      },

      error: (err) => {

  console.log("Register error:", err);

  this.toast.error(err?.error?.message || "Registration failed");

}

    });

  }

}
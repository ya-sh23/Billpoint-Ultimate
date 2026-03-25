import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss'
})
export class StaffDashboardComponent implements OnInit {
  activeTab = 'billing'; // billing, customers, attendance
  
  // Billing
  customerPhoneSearch = '';
  selectedCustomer: any = null;
  products: any[] = [];
  cart: any[] = [];
  paymentMode = 'CASH';
  discountInput = 0; // The amount or percentage value
  discountType = 'PERCENTAGE'; // PERCENTAGE or AMOUNT
  showQRModal = false;
  paymentProcessing = false;
  paymentSuccess = false;

  // New Customer
  newCustomer: any = {};

  private apiUrl = 'http://localhost:8080/api';

  constructor(
    public authService: AuthService, 
    private http: HttpClient,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadAttendance();
    this.loadRecentBills();
    // Use dedicated staff endpoint for inventory access
    this.http.get<any[]>(`${this.apiUrl}/staff/products`).subscribe({
      next: (data) => this.products = data,
      error: () => console.log('Error loading products for staff.')
    });
  }

  // --- Attendance ---
  attendance = {
    date: new Date().toISOString().split('T')[0],
    workingHours: 0,
    attendanceCode: ''
  };
  attendanceHistory: any[] = [];
  recentBills: any[] = [];

  loadAttendance() {
    const userRole = localStorage.getItem('userRole');
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (userRole === 'STAFF' && user.id) {
      // Find staffId from userId
      this.http.get<any>(`${this.apiUrl}/staff/profile`).subscribe({
        next: (staff) => {
          this.http.get<any[]>(`${this.apiUrl}/attendance/staff/${staff.id}`).subscribe(data => {
            this.attendanceHistory = data;
          });
        },
        error: () => console.error('Could not load staff profile')
      });
    }
  }

  markAttendance() {
    if (this.attendance.workingHours < 0 || this.attendance.workingHours > 9) {
      this.toast.error('Working hours must be between 0 and 9.');
      return;
    }

    this.http.get<any>(`${this.apiUrl}/staff/profile`).subscribe({
      next: (staff) => {
        const payload = {
          staff: { id: staff.id },
          date: new Date().toISOString().split('T')[0], // Enforce today's date
          workingHours: this.attendance.workingHours,
          attendanceCode: this.attendance.attendanceCode
        };
        this.http.post(`${this.apiUrl}/attendance/mark`, payload).subscribe({
          next: (res: any) => {
            this.toast.success(res.message);
            this.attendance.attendanceCode = '';
            this.loadAttendance();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error marking attendance')
        });
      }
    });
  }

  downloadInvoice(billId: number) {
    this.http.get(`${this.apiUrl}/staff/bills/${billId}/invoice`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `invoice_${billId}.pdf`;
        a.click();
      },
      error: (err) => this.toast.error('Error downloading invoice: ' + err.error?.message)
    });
  }

  // --- Customers ---
  searchCustomer() {
    this.http.get<any>(`${this.apiUrl}/staff/customers/search?phone=${this.customerPhoneSearch}`).subscribe({
      next: (data) => {
        this.selectedCustomer = data;
        this.toast.success('Customer found: ' + data.name);
      },
      error: () => this.toast.error('Customer not found')
    });
  }

  addCustomer() {
    this.http.post<any>(`${this.apiUrl}/staff/customers`, this.newCustomer).subscribe({
      next: (data) => {
        this.toast.success('Customer registered successfully!');
        this.selectedCustomer = data;
        this.newCustomer = {};
        this.activeTab = 'billing'; // go back to billing
      },
      error: (err) => this.toast.error('Error: ' + err.error?.message)
    });
  }

  // --- Billing ---
  addToCart(product: any) {
    const item = this.cart.find(i => i.product.id === product.id);
    if (item) {
      item.quantity += 1;
      item.totalPrice = item.quantity * item.pricePerUnit;
    } else {
      this.cart.push({
        product: { id: product.id, name: product.name },
        quantity: 1,
        pricePerUnit: product.price,
        totalPrice: product.price
      });
    }
  }

  updateQuantity(index: number, delta: number) {
    const item = this.cart[index];
    item.quantity += delta;
    if (item.quantity <= 0) {
      this.removeFromCart(index);
    } else {
      item.totalPrice = item.quantity * item.pricePerUnit;
    }
  }

  removeFromCart(index: number) {
    this.cart.splice(index, 1);
  }

  get cartTotal() {
    return this.cart.reduce((sum, item) => sum + item.totalPrice, 0);
  }

  get discountAmount() {
    if (this.discountType === 'PERCENTAGE') {
      return (this.cartTotal * this.discountInput) / 100;
    }
    return this.discountInput;
  }

  get finalAmount() {
    return Math.max(0, this.cartTotal - this.discountAmount);
  }

  generateBill() {
    if (!this.selectedCustomer) {
      this.toast.warning('Please select or create a customer first.');
      return;
    }
    if (this.cart.length === 0) {
      this.toast.warning('Cart is empty.');
      return;
    }

    if (this.paymentMode === 'UPI' || this.paymentMode === 'CARD') {
      this.initiateQRPayment();
    } else {
      this.processBill();
    }
  }

  initiateQRPayment() {
    this.showQRModal = true;
    this.paymentProcessing = true;
    this.paymentSuccess = false;

    // Simulate 3s payment processing
    setTimeout(() => {
      this.paymentProcessing = false;
      this.paymentSuccess = true;
      this.toast.success('Payment Successful!');
      
      // Auto-close and process after 1.5s success view
      setTimeout(() => {
        this.showQRModal = false;
        this.processBill();
      }, 1500);
    }, 3000);
  }

  processBill() {
    const payload = {
      customerId: this.selectedCustomer.id,
      totalAmount: this.cartTotal,
      discount: this.discountAmount,
      discountPercentage: this.discountType === 'PERCENTAGE' ? this.discountInput : null,
      finalAmount: this.finalAmount,
      paymentMode: this.paymentMode,
      items: this.cart
    };

    this.http.post(`${this.apiUrl}/staff/bills`, payload).subscribe({
      next: (res: any) => {
        this.toast.success(res.message);
        this.cart = [];
        this.selectedCustomer = null;
        this.customerPhoneSearch = '';
        this.discountInput = 0;
        this.loadRecentBills();
      },
      error: (err) => this.toast.error('Error creating bill: ' + err.error?.message)
    });
  }

  loadRecentBills() {
    this.http.get<any[]>(`${this.apiUrl}/reports/sales?startDate=${new Date().toISOString().split('T')[0]}&endDate=${new Date().toISOString().split('T')[0]}`).subscribe({
      next: (data) => this.recentBills = data,
      error: () => console.error('Could not load recent bills')
    });
  }

  logout() {
    this.authService.logout();
  }
}

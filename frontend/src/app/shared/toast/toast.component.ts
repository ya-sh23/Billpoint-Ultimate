import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, ToastMessage } from '../../services/toast.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      <div *ngFor="let toast of toasts; let i = index" 
           class="toast-item" 
           [ngClass]="toast.type"
           (click)="removeToast(i)">
        <div class="toast-icon">
          <i *ngIf="toast.type === 'success'" class="fas fa-check-circle"></i>
          <i *ngIf="toast.type === 'error'" class="fas fa-exclamation-circle"></i>
          <i *ngIf="toast.type === 'info'" class="fas fa-info-circle"></i>
          <i *ngIf="toast.type === 'warning'" class="fas fa-exclamation-triangle"></i>
        </div>
        <div class="toast-content">
          {{ toast.text }}
        </div>
        <div class="toast-close">&times;</div>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 1rem;
      right: 1rem;
      z-index: 2147483647;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      pointer-events: none;
    }

    .toast-item {
      pointer-events: auto;
      min-width: 250px;
      padding: 1rem 1.25rem;
      border-radius: 12px;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
      display: flex;
      align-items: center;
      gap: 1rem;
      cursor: pointer;
      animation: slideIn 0.3s ease-out forwards;
      border-left: 5px solid #ccc;
      font-family: 'Outfit', sans-serif;
      font-weight: 500;
    }

    .toast-item.success { border-left-color: #10b981; color: #064e3b; }
    .toast-item.error { border-left-color: #ef4444; color: #7f1d1d; }
    .toast-item.info { border-left-color: #3b82f6; color: #1e3a8a; }
    .toast-item.warning { border-left-color: #f59e0b; color: #78350f; }

    .toast-icon { font-size: 1.25rem; }
    .toast-content { flex: 1; }
    .toast-close { opacity: 0.5; font-size: 1.25rem; }

    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }

    @keyframes fadeOut {
      from { opacity: 1; }
      to { opacity: 0; }
    }
  `]
})
export class ToastComponent implements OnInit, OnDestroy {
  toasts: ToastMessage[] = [];
  private subscription: Subscription = new Subscription();

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.subscription = this.toastService.toastState.subscribe(toast => {
      this.toasts.push(toast);
      setTimeout(() => this.removeToast(0), 5000);
    });
  }

  removeToast(index: number) {
    this.toasts.splice(index, 1);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}

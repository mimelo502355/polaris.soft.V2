import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, Notification } from '../../service/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="notification" [ngClass]="'notification notification-' + notification.type">
      <div class="notification-content">
        <span class="notification-icon">
          <span *ngIf="notification.type === 'success'">✅</span>
          <span *ngIf="notification.type === 'error'">❌</span>
          <span *ngIf="notification.type === 'warning'">⚠️</span>
          <span *ngIf="notification.type === 'info'">ℹ️</span>
        </span>
        <span class="notification-message">{{ notification.message }}</span>
      </div>
    </div>
  `,
  styles: [`
    .notification {
      position: fixed;
      top: 20px;
      right: 20px;
      max-width: 400px;
      padding: 14px 16px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      display: flex;
      align-items: center;
      z-index: 9999;
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .notification-success {
      background: #d4edda;
      color: #155724;
      border-left: 4px solid #28a745;
    }

    .notification-error {
      background: #f8d7da;
      color: #721c24;
      border-left: 4px solid #dc3545;
    }

    .notification-warning {
      background: #fff3cd;
      color: #856404;
      border-left: 4px solid #ffc107;
    }

    .notification-info {
      background: #d1ecf1;
      color: #0c5460;
      border-left: 4px solid #17a2b8;
    }

    .notification-content {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .notification-icon {
      font-size: 18px;
      flex-shrink: 0;
    }

    .notification-message {
      font-size: 14px;
      line-height: 1.4;
    }
  `]
})
export class NotificationComponent implements OnInit {
  notification: Notification | null = null;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.notification$.subscribe((notification: Notification | null) => {
      this.notification = notification;
    });
  }
}
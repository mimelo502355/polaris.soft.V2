import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSubject = new BehaviorSubject<Notification | null>(null);
  public notification$ = this.notificationSubject.asObservable();

  show(notification: Notification): void {
    this.notificationSubject.next(notification);
    
    if (notification.duration) {
      setTimeout(() => this.notificationSubject.next(null), notification.duration);
    }
  }

  success(message: string, duration = 3000): void {
    this.show({ type: 'success', message, duration });
  }

  error(message: string, duration = 5000): void {
    this.show({ type: 'error', message, duration });
  }

  warning(message: string, duration = 4000): void {
    this.show({ type: 'warning', message, duration });
  }

  info(message: string, duration = 3000): void {
    this.show({ type: 'info', message, duration });
  }

  clear(): void {
    this.notificationSubject.next(null);
  }
}
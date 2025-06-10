import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface ReservationForm {
  name: string;
  phone: string;
  email: string;
  date: string;
  time: string;
  guests: number;
  specialRequests: string;
}

@Component({
  selector: 'app-reservation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css'],
})
export class ReservationComponent {
  isSubmitted = false;
  isLoading = false;

  reservationForm: ReservationForm = {
    name: '',
    phone: '',
    email: '',
    date: '',
    time: '',
    guests: 2,
    specialRequests: '',
  };

  timeSlots = [
    '5:00 PM',
    '5:30 PM',
    '6:00 PM',
    '6:30 PM',
    '7:00 PM',
    '7:30 PM',
    '8:00 PM',
    '8:30 PM',
    '9:00 PM',
    '9:30 PM',
  ];

  guestOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  // Get today's date in YYYY-MM-DD format for min date
  get minDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  onSubmit() {
    if (this.isValidForm()) {
      this.isLoading = true;

      // Simulate API call
      setTimeout(() => {
        this.isLoading = false;
        this.isSubmitted = true;
      }, 2000);
    }
  }

  isValidForm(): boolean {
    return !!(
      this.reservationForm.name &&
      this.reservationForm.phone &&
      this.reservationForm.email &&
      this.reservationForm.date &&
      this.reservationForm.time &&
      this.reservationForm.guests
    );
  }

  resetForm() {
    this.isSubmitted = false;
    this.reservationForm = {
      name: '',
      phone: '',
      email: '',
      date: '',
      time: '',
      guests: 2,
      specialRequests: '',
    };
  }
}

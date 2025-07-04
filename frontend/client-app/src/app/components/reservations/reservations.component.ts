// src/app/reservations/reservations.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { ReservationService } from '../../services/reservation.service';
import { UserReservationRequest } from '../../models/reservation/user-reservation-request.model';
// NOU: Importăm serviciul și interfața pentru n8n
import {
  ReservationN8nService,
  ReservationData,
} from '../../services/reservation-n8n.service';

@Component({
  selector: 'app-reservations',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css'],
})
export class ReservationsComponent implements OnInit {
  reservationForm: FormGroup;
  isSubmitting = false;
  minDate = new Date();

  // NOU: Stare separată pentru butonul de solicitare apel
  isRequestingCall = false;

  private readonly allTimeSlots = [
    '12:00',
    '12:30',
    '13:00',
    '13:30',
    '14:00',
    '14:30',
    '19:00',
    '19:30',
    '20:00',
    '20:30',
    '21:00',
    '21:30',
    '22:00',
  ];

  filteredTimeSlots: string[] = [];
  partySize = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private reservationService: ReservationService,
    // Injectăm serviciul n8n
    private reservationN8nService: ReservationN8nService
  ) {
    this.reservationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\-\s()]+$/)]],
      date: [new Date(), Validators.required],
      time: ['', Validators.required],
      guests: [2, [Validators.required, Validators.min(1), Validators.max(10)]],
      specialRequests: [''],
    });
  }

  ngOnInit(): void {
    this.filterTimeSlots(this.reservationForm.get('date')?.value);
    this.reservationForm.get('date')?.valueChanges.subscribe((selectedDate) => {
      this.filterTimeSlots(selectedDate);
    });
  }

  private filterTimeSlots(selectedDate: Date | null): void {
    if (!selectedDate) {
      this.filteredTimeSlots = [];
      return;
    }
    const now = new Date();
    const isToday = selectedDate.toDateString() === now.toDateString();
    if (isToday) {
      const currentHour = now.getHours();
      const currentMinute = now.getMinutes();
      this.filteredTimeSlots = this.allTimeSlots.filter((time) => {
        const [slotHour, slotMinute] = time.split(':').map(Number);
        return (
          slotHour > currentHour ||
          (slotHour === currentHour && slotMinute > currentMinute)
        );
      });
    } else {
      this.filteredTimeSlots = [...this.allTimeSlots];
    }
    const currentTimeValue = this.reservationForm.get('time')?.value;
    if (
      currentTimeValue &&
      !this.filteredTimeSlots.includes(currentTimeValue)
    ) {
      this.reservationForm.get('time')?.setValue('');
    }
  }

  // Metoda pentru butonul principal de rezervare (rămâne neschimbată)
  onSubmit() {
    if (this.reservationForm.valid) {
      this.isSubmitting = true;
      const formValue = this.reservationForm.value;
      const selectedDate: Date = formValue.date;
      const selectedTime: string = formValue.time;

      const year = selectedDate.getFullYear();
      const month = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
      const day = selectedDate.getDate().toString().padStart(2, '0');
      const formattedDate = `${year}-${month}-${day}`;
      const reservationDateTime = `${formattedDate}T${selectedTime}:00`;

      const reservationRequest: UserReservationRequest = {
        customerName: `${formValue.firstName} ${formValue.lastName}`,
        phoneNumber: formValue.phone,
        reservationDateTime: reservationDateTime,
        numberOfPeople: formValue.guests,
      };

      this.reservationService
        .createUserReservation(reservationRequest)
        .subscribe({
          next: (response) => {
            this.isSubmitting = false;
            this.snackBar.open(
              'Rezervarea a fost trimisă cu succes! Vă vom contacta în curând pentru confirmare.',
              'Închide',
              { duration: 5000, panelClass: ['success-snackbar'] }
            );
            this.reservationForm.reset({ date: new Date(), guests: 2 });
            this.filterTimeSlots(new Date());
          },
          error: (error) => {
            this.isSubmitting = false;
            console.error('Eroare la trimiterea rezervării:', error);
            this.snackBar.open(
              'A apărut o eroare la trimiterea rezervării.',
              'Închide',
              { duration: 7000, panelClass: ['error-snackbar'] }
            );
          },
        });
    } else {
      this.markFormGroupTouched();
      this.snackBar.open(
        'Vă rugăm să completați corect toate câmpurile obligatorii.',
        'Închide',
        { duration: 5000, panelClass: ['error-snackbar'] }
      );
    }
  }

  // NOU: Metodă pentru a solicita un apel prin n8n, independent de formular
  requestACall() {
    this.isRequestingCall = true;

    const callRequestPayload: ReservationData = {
      customerName: 'Client-Solicitare-Apel',
      phoneNumber: 'N/A - Sunați înapoi',
      reservationDateTime: new Date().toISOString(), // Trimitem data curentă pentru context
      numberOfPeople: 0,
    };

    this.reservationN8nService
      .triggerReservationWorkflow(callRequestPayload)
      .subscribe({
        next: (response) => {
          this.isRequestingCall = false;
          this.snackBar.open(
            'Solicitarea a fost trimisă! Vă vom contacta în cel mai scurt timp.',
            'OK',
            { duration: 5000, panelClass: ['success-snackbar'] }
          );
        },
        error: (error) => {
          this.isRequestingCall = false;
          console.error('Eroare la solicitarea apelului:', error);
          this.snackBar.open(
            'Nu am putut trimite solicitarea. Vă rugăm încercați mai târziu.',
            'Închide',
            { duration: 7000, panelClass: ['error-snackbar'] }
          );
        },
      });
  }

  private markFormGroupTouched() {
    Object.values(this.reservationForm.controls).forEach((control) => {
      control.markAsTouched();
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.reservationForm.get(fieldName);
    if (control?.hasError('required')) return 'Acest câmp este obligatoriu';
    if (control?.hasError('email'))
      return 'Introduceți o adresă de email validă';
    if (control?.hasError('minlength')) return 'Prea scurt';
    if (control?.hasError('pattern')) return 'Format invalid';
    if (control?.hasError('min')) return 'Valoare prea mică';
    if (control?.hasError('max')) return 'Valoare prea mare';
    return '';
  }
}

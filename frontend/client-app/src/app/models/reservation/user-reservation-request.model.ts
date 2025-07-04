// src/app/models/reservation/user-reservation-request.model.ts
export interface UserReservationRequest {
  customerName: string;
  phoneNumber: string;
  reservationDateTime: string;
  numberOfPeople: number;
}

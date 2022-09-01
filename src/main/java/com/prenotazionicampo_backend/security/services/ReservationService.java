package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;

import java.util.List;

public interface ReservationService {

    List<Reservation> findAllReservation();

    Reservation saveReservation(Reservation reservation);


}

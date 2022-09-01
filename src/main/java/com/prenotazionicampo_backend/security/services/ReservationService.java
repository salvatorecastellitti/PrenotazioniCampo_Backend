package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getAll();

    Reservation saveReservation(Reservation reservation);

    Optional<Reservation> findById(Long id);

}

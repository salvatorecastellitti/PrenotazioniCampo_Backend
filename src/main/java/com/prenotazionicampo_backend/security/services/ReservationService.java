package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getAll();

    List<Reservation> getReservationByDateAndField(Date sDate, Date eDate, Long fieldId);

    Reservation saveReservation(Reservation reservation);

    Optional<Reservation> findById(Long id);

    Reservation updateReservation(Reservation reservation);

}

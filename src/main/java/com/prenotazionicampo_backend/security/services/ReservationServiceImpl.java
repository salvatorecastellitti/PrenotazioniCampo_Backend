package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> findAllReservation() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}

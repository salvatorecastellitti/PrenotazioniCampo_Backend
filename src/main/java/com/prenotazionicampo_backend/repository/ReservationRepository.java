package com.prenotazionicampo_backend.repository;

import com.prenotazionicampo_backend.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

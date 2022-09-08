package com.prenotazionicampo_backend.repository;

import com.prenotazionicampo_backend.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT * from reservations where start_date > :sDate and end_date < :eDate and field = :fieldId", nativeQuery = true)
    List<Reservation> findReservationByDateAndField(Date sDate, Date eDate, Long fieldId);
}

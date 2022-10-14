package com.prenotazionicampo_backend.repository;

import com.prenotazionicampo_backend.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //usata
    @Query(value = "SELECT * from reservations where start_date >= :sDate and end_date <= :eDate and field = :fieldId order by start_date", nativeQuery = true)
    List<Reservation> findReservationByDateAndField(Date sDate, Date eDate, Long fieldId);

    //usata
    @Query(value = "select * from reservations where user = :userId and start_date >= :sDate and end_date <= :eDate order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerUserAndPerDay(Date sDate, Date eDate, Long userId);

    //usata
    @Query(value = "select * from reservations where user = :userId order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerUser(Long userId);

    @Query(value = "select * from reservations where user = :userId and field = :fieldId order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerUserAndPerField(Long userId, Long fieldId);

    @Query(value = "select * from reservations where user = :userId and field = :fieldId and start_date >= :sDate and end_date <= :eDate order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerUserAndPerFieldAndDay(Date sDate, Date eDate, Long userId, Long fieldId);

    @Query(value = "select * from reservations where start_date >= :sDate and end_date <= :eDate order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerDate(Date sDate, Date eDate);

    @Query(value = "select * from reservations where field = :fieldId order by start_date", nativeQuery = true)
    List<Reservation> findReservationPerField(Long fieldId);
}

package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationByDateAndField(Date sDate, Date eDate, Long fieldId) {

        return reservationRepository.findReservationByDateAndField(sDate, eDate, fieldId);
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> findReservationPerUserAndPerDay(Date sDate, Date eDate, Long userId) {

        return reservationRepository.findReservationPerUserAndPerDay(sDate, eDate, userId);
    }

    @Override
    public List<Reservation> findReservationPerUser(Long userId) {
        return reservationRepository.findReservationPerUser(userId);
    }

    @Override
    public List<Reservation> findReservationPerUserAndPerField(Long userId, Long fieldId) {
        return reservationRepository.findReservationPerUserAndPerField(userId,fieldId);
    }

    @Override
    public List<Reservation> findReservationPerUserAndPerFieldAndDay(Date sDate, Date eDate, Long userId, Long fieldId) {
        return reservationRepository.findReservationPerUserAndPerFieldAndDay(sDate, eDate, userId,fieldId);
    }

    @Override
    public List<Reservation> findReservationPerDate(Date sDate, Date eDate) {
        return reservationRepository.findReservationPerDate(sDate,eDate);
    }

    @Override
    public List<Reservation> findReservationPerField(Long fieldId) {
        return reservationRepository.findReservationPerField(fieldId);
    }


}

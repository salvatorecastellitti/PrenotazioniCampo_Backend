package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
}

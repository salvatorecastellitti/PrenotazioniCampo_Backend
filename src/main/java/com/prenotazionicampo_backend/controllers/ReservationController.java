package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import com.prenotazionicampo_backend.security.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reservations/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/list")
    public List<Reservation> getAllReservation(){
        return reservationService.getAll();
    }

    @PostMapping("/add")
    public Reservation createReservation(@RequestBody Reservation reservation){
        return reservationService.saveReservation(reservation);
    }

    @PostMapping("/update")
    public Reservation updateUser(@RequestBody Reservation reservation){
        return reservationService.updateReservation(reservation);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id){
        Reservation reservation = reservationService.findById(id).orElseThrow(()->new ResourceNotFoundException("Reservation not found with id: "+id));
        return  ResponseEntity.ok(new MessageResponse("Reservation added successfully!"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteReservation(@PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("reservation not exist with id :" + id ));

        reservationRepository.delete(reservation);
        Map<String,Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}

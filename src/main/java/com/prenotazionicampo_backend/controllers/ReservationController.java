package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.InvalidDateException;
import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import com.prenotazionicampo_backend.security.services.FieldService;
import com.prenotazionicampo_backend.security.services.ReservationService;
import com.prenotazionicampo_backend.security.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/reservations/")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FieldService fieldService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Reservation> getAllReservation(){
        return reservationService.getAll();
    }

    @GetMapping("/list/{date}/{fieldId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Reservation> getReservationByDay(@PathVariable String date, @PathVariable Long fieldId){
        try{
            String newDate = date.replace('-','/');
            Date sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(newDate+" 00:01");
            Date eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(newDate+" 23:59");
            if (eDate.before(sDate)){
                throw new InvalidDateException("EndDate: " + eDate + " can't be greater than StartDate: " + sDate);
            }
            return reservationService.getReservationByDateAndField(sDate, eDate, fieldId);
        }catch (java.text.ParseException e){
            throw new InvalidDateException("Error parsing value: " + date + " date format required: 'gg-MM-yyyy'");
        }



    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation){

        User user = userService.findById(reservation.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reservation.getUserId()));
        Field field = fieldService.findById(reservation.getFieldId()).orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + reservation.getFieldId()));
        if(reservation.getEndDate().before(reservation.getStartDate())){
            throw new InvalidDateException("EndDate: " + reservation.getEndDate() + " can't be greater than StartDate: " + reservation.getStartDate());
        }
        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation correctly added for username: '" + user.getUsername() + "' on field: '" + field.getName()+"'");
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateReservation(@RequestBody Reservation reservation){
        if(reservation.getEndDate().before(reservation.getStartDate())){
            throw new InvalidDateException("EndDate: " + reservation.getEndDate() + " can't be greater than StartDate: " + reservation.getStartDate());
        }
        reservationService.updateReservation(reservation);
        return ResponseEntity.ok("Reservation updated");
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id){
        Reservation reservation = reservationService.findById(id).orElseThrow(()->new ResourceNotFoundException("Reservation not found with id: "+id));
        return  ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteReservation(@PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reservation not exist with id :" + id ));

        reservationRepository.delete(reservation);
        Map<String,Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}

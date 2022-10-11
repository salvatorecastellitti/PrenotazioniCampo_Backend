package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.InvalidDateException;
import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.models.Reservation;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.payload.ReservationProfile.ReservationHolder;
import com.prenotazionicampo_backend.payload.ReservationProfile.ReservationTemplateForGet;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.repository.ReservationRepository;
import com.prenotazionicampo_backend.security.services.FieldService;
import com.prenotazionicampo_backend.security.services.ReservationService;
import com.prenotazionicampo_backend.security.services.UserService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/reservations/")
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
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
    public List<Reservation> getAllReservation() throws IOException {
        List<Reservation> reservations=  reservationService.getAll();
        for (Reservation reservation: reservations){
            setPhoto(reservation);
        }
        return reservations;
    }


    @GetMapping("/getReservationWithFilter")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getReservationWithFilter(@RequestBody ReservationTemplateForGet reservationTemplateForGet) throws ParseException, IOException {
        Date eDate = null;
        Date sDate = null;
        List<Reservation> reservations;
        if(reservationTemplateForGet.getStartDate()!=null) {
            if(reservationTemplateForGet.getStartDate().length()>11) {
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate());
            }else{
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate() + " 00:01");
            }

            if(reservationTemplateForGet.getEndDate()==null){
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate().substring(0,11) + " 23:59");
            } else if (reservationTemplateForGet.getEndDate().length()>11) {
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate());
            }else{
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate() + " 23:59");
            }
            log.info(sDate);
            log.info(eDate);
/*
            if (reservationTemplateForGet.getEndDate() == null) {
                if(reservationTemplateForGet.getStartDate().length()>11){
                    sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate());
                    eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate());
                }else{
                    sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate() + " 00:01");
                    eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate() + " 23:59");
                }

            } else {
                if(reservationTemplateForGet.getEndDate().length()>11) {
                    sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate());
                    eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate());
                }else{
                    sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate() + " 00:01");
                    eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate() + " 23:59");
                }

            }*/
            if (eDate.before(sDate)) {
                throw new InvalidDateException("EndDate: " + eDate + " can't be greater than StartDate: " + sDate);
            }
        }

        if(reservationTemplateForGet.getUserId() == null && reservationTemplateForGet.getStartDate()!=null && reservationTemplateForGet.getFieldId()!=null){
            reservations = reservationService.getReservationByDateAndField(sDate, eDate, reservationTemplateForGet.getFieldId());
        }
        else if(reservationTemplateForGet.getUserId() != null && reservationTemplateForGet.getStartDate()!=null && reservationTemplateForGet.getFieldId()==null){
            reservations = reservationService.findReservationPerUserAndPerDay(sDate, eDate, reservationTemplateForGet.getUserId());
        }
        else if(reservationTemplateForGet.getUserId() != null && reservationTemplateForGet.getStartDate()==null && reservationTemplateForGet.getFieldId()==null){
            reservations = reservationService.findReservationPerUser(reservationTemplateForGet.getUserId());
        }else if(reservationTemplateForGet.getUserId() != null && reservationTemplateForGet.getStartDate()==null && reservationTemplateForGet.getFieldId()!=null){
            reservations = reservationService.findReservationPerUserAndPerField(reservationTemplateForGet.getUserId(), reservationTemplateForGet.getFieldId());

        }else if(reservationTemplateForGet.getUserId() != null && reservationTemplateForGet.getStartDate()!=null && reservationTemplateForGet.getFieldId()!=null) {
            reservations = reservationService.findReservationPerUserAndPerFieldAndDay(sDate, eDate, reservationTemplateForGet.getUserId(), reservationTemplateForGet.getFieldId());
        }else if(reservationTemplateForGet.getFieldId()==null && reservationTemplateForGet.getUserId()==null && reservationTemplateForGet.getStartDate()!=null){
            reservations = reservationService.findReservationPerDate(sDate,eDate);
        }else if(reservationTemplateForGet.getFieldId()!=null && reservationTemplateForGet.getUserId()==null && reservationTemplateForGet.getStartDate()==null){
            reservations = reservationService.findReservationPerField(reservationTemplateForGet.getFieldId());
        }else{
            return ResponseEntity.badRequest().body(new MessageResponse("Qualcosa è andato storto, riprova"));
        }


        for (Reservation reservation: reservations){
            setPhoto(reservation);
        }
        return ResponseEntity.ok(reservations);

    }
    /*
    @GetMapping("/getAllPerDayAndField")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getReservationByDay(@RequestBody ReservationTemplateForGet reservationTemplateForGet){
        Date eDate;
        Date sDate;
        try{
            if(reservationTemplateForGet.getEndDate() == null){
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 23:59");

            }else{
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate()+" 23:59");
            }
            if (eDate.before(sDate)){
                throw new InvalidDateException("EndDate: " + eDate + " can't be greater than StartDate: " + sDate);
            }
            return ResponseEntity.ok(reservationService.getReservationByDateAndField(sDate, eDate, reservationTemplateForGet.getFieldId()));
        }catch (java.text.ParseException e){
            throw new InvalidDateException("Error parsing value: " + reservationTemplateForGet.getStartDate() + " date format required: 'gg/MM/yyyy'");
        }
    }

    @GetMapping("/getPerUserAndPerDay")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findReservationPerUserAndPerDay(@RequestBody ReservationTemplateForGet reservationTemplateForGet){
        Date eDate;
        Date sDate;
        try{
            if(reservationTemplateForGet.getEndDate() == null){
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 23:59");

            }else{
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate()+" 23:59");
            }
            if (eDate.before(sDate)){
                throw new InvalidDateException("EndDate: " + eDate + " can't be greater than StartDate: " + sDate);
            }
            return ResponseEntity.ok(reservationService.findReservationPerUserAndPerDay(sDate, eDate, reservationTemplateForGet.getUserId()));
        }catch (java.text.ParseException e){
            throw new InvalidDateException("Error parsing value: " + reservationTemplateForGet.getStartDate() + " date format required: 'gg/MM/yyyy'");
        }
    }
    @GetMapping("/getAllPerUserId/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findReservationPerUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.findReservationPerUser(userId);
        for(Reservation reservation: reservations){
            reservation.setUser(null);
        }
        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/getAllPerUserAndField")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findReservationPerUserAndPerField(@RequestBody ReservationTemplateForGet reservationTemplateForGet){
        return ResponseEntity.ok(reservationService.findReservationPerUserAndPerField(reservationTemplateForGet.getUserId(), reservationTemplateForGet.getFieldId()));
    }

    @GetMapping("/getAllPerUserAndFieldAndDay")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> finReservationPerUserPerFieldAndPerDay(@RequestBody ReservationTemplateForGet reservationTemplateForGet){
        Date eDate;
        Date sDate;
        try{
            if(reservationTemplateForGet.getEndDate() == null){
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 23:59");

            }else{
                sDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getStartDate()+" 00:01");
                eDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reservationTemplateForGet.getEndDate()+" 23:59");
            }
            if (eDate.before(sDate)){
                throw new InvalidDateException("EndDate: " + eDate + " can't be greater than StartDate: " + sDate);
            }
            return ResponseEntity.ok(reservationService.findReservationPerUserAndPerFieldAndDay(sDate, eDate, reservationTemplateForGet.getUserId(), reservationTemplateForGet.getFieldId()));
        }catch (java.text.ParseException e){
            throw new InvalidDateException("Error parsing value: " + reservationTemplateForGet.getStartDate() + " date format required: 'gg/MM/yyyy'");
        }

    }
*/
    //devo procare a creare un pyload per la add, e poi nelle get restituire tutto
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createReservation(@RequestBody ReservationHolder reservationHolder){
        User user = userService.findById(reservationHolder.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reservationHolder.getUserId()));
        Field field = fieldService.findById(reservationHolder.getFieldId()).orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + reservationHolder.getFieldId()));

        Reservation reservation = new Reservation(reservationHolder.getStartDate(), reservationHolder.getEndDate(), user,field);
        if(reservation.getEndDate().before(reservation.getStartDate())){
            throw new InvalidDateException("EndDate: " + reservation.getEndDate() + " can't be greater than StartDate: " + reservation.getStartDate());
        }
        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation correctly added for username: '" + reservation.getUser().getUsername() + "' on field: '" + reservation.getField().getName()+"'");
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
    public ResponseEntity<?> getReservationById(@PathVariable Long id) throws IOException {
        Reservation reservation = reservationService.findById(id).orElseThrow(()->new ResourceNotFoundException("Reservation not found with id: "+id));
        setPhoto(reservation);

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

    private void setPhoto(Reservation reservation) throws IOException {
        if (reservation.getUser().getPhotos() != null){
            File img = new File("/etc/testSpring/user-photos/" + reservation.getUser().getId() + "/" + reservation.getUser().getPhotos());
            reservation.getUser().setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        if(reservation.getField().getPhotos()!= null){
            File img = new File("/etc/testSpring/field-photos/" + reservation.getField().getId() + "/" + reservation.getField().getPhotos());
            reservation.getField().setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
    }
}

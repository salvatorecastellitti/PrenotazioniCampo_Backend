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

            if (eDate.before(sDate)) {
                return ResponseEntity.badRequest().body(new MessageResponse("La data di fine non può essere maggiore della data di inzio",400));
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
            return ResponseEntity.badRequest().body(new MessageResponse("Qualcosa è andato storto, riprova",400));
        }


        for (Reservation reservation: reservations){
            setPhoto(reservation);
        }
        return ResponseEntity.ok(reservations);

    }
    //devo procare a creare un pyload per la add, e poi nelle get restituire tutto
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createReservation(@RequestBody ReservationHolder reservationHolder){
        User user = userService.findById(reservationHolder.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per id: " + reservationHolder.getUserId()));
        Field field = fieldService.findById(reservationHolder.getFieldId()).orElseThrow(() -> new ResourceNotFoundException("Campo non trovato per id: " + reservationHolder.getFieldId()));

        Reservation reservation = new Reservation(reservationHolder.getStartDate(), reservationHolder.getEndDate(), user,field);
        if(reservation.getEndDate().before(reservation.getStartDate())){
            return ResponseEntity.badRequest().body(new MessageResponse("La data di fine non può essere maggiore della data di inzio",400));
        }
        reservationRepository.save(reservation);
        return ResponseEntity.ok(new MessageResponse("Prenotazione aggiunta per utente: "+ reservation.getUser().getUsername() + " per il campo " +   reservation.getField().getName()));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateReservation(@RequestBody Reservation reservation){
        if(reservation.getEndDate().before(reservation.getStartDate())){
            return ResponseEntity.badRequest().body(new MessageResponse("La data di fine non può essere maggiore della data di inzio",400));
        }
        reservationService.updateReservation(reservation);
        return ResponseEntity.ok(new MessageResponse("Prenotazione aggiornata",200));
    }


    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) throws IOException {
        Reservation reservation = reservationService.findById(id).orElseThrow(()->new ResourceNotFoundException("Prenotazione non trovata per id: "+id));
        setPhoto(reservation);

        return  ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Prenotazione non trovata con id:" + id ));

        reservationRepository.delete(reservation);
        return ResponseEntity.ok(new MessageResponse("Prenotazione eliminata",200));
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

package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.payload.userProfile.FieldHolder;
import com.prenotazionicampo_backend.payload.userProfile.PhotoHolder;
import com.prenotazionicampo_backend.repository.FieldRepository;
import com.prenotazionicampo_backend.security.services.FieldService;
import com.prenotazionicampo_backend.util.FileUploadUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("/api/v1/fields/")
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
public class FieldController {
    @Autowired
    private FieldService fieldService;

    @Autowired
    private FieldRepository fieldRepository;
    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Field> getListFields() throws IOException {
        List<Field> fields =  fieldService.findAll();
        for (Field field : fields) {
            if (field.getPhotos() != null) {
                File img = new File("/etc/testSpring/field-photos/" + field.getId() + "/" + field.getPhotos());
                field.setPhotoMedia(FileUtils.readFileToByteArray(img));
            }
        }
        return fields;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Field createField(@RequestBody Field field){
        return fieldService.saveField(field);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Field updateUser(@RequestBody FieldHolder fieldHolder){
        Field field = fieldService.findById(Long.valueOf(fieldHolder.getId())).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+fieldHolder.getId()));
        if (fieldHolder.getCountry()!= null){
            field.setCountry(fieldHolder.getCountry());
        }
        if(fieldHolder.getAddress()!=null){
            field.setAddress(field.getAddress());
        }
        if(fieldHolder.getName()!=null){
            field.setName(fieldHolder.getName());
        }
        return fieldService.updateField(field);
    }

    @PostMapping("/changePhoto/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changePhoto(@RequestBody PhotoHolder photoHolder, @PathVariable Long id) throws IOException {
        String filename = StringUtils.cleanPath(photoHolder.getImageName());
        Field field = fieldService.findById(Long.valueOf(id)).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        field.setPhotos(filename);

        try{
            String photoMedia = photoHolder.getImage().substring(photoHolder.getImage().indexOf(",")+1);
            byte[] imageByte= Base64.getDecoder().decode(photoMedia);
            String uploadDir = "/etc/testSpring/field-photos/"+field.getId();
            FileUploadUtil.saveFile(uploadDir, filename, imageByte);
        }catch (Exception e){
            log.error(e);
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile salvare la foto",400));
        }
        try{
            fieldService.saveField(field);
            return ResponseEntity.ok(new MessageResponse("Foto aggiunta correttamente",200));
        }catch (Exception e){
            log.error(e);
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile aggiornare la foto", 400));
        }


    }
    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) throws IOException {
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Campo non trovato con id: "+id));
        if(field.getPhotos()!= null){
            File img = new File("/etc/testSpring/field-photos/" + field.getId() + "/" + field.getPhotos());
            field.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(field);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteField(@PathVariable Long id){
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Campo non trovato con id: "+id));
        fieldRepository.delete(field);
        return ResponseEntity.ok(new MessageResponse("Campo eliminato correttamente",200));
    }


}
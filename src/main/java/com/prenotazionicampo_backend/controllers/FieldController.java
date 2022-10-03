package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.repository.FieldRepository;
import com.prenotazionicampo_backend.security.services.FieldService;
import com.prenotazionicampo_backend.util.FileUploadUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fields/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FieldController {
    @Autowired
    private FieldService fieldService;

    @Autowired
    private FieldRepository fieldRepository;
    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Field> getListFields(){
        return fieldService.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Field createField(@RequestBody Field field){
        return fieldService.saveField(field);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Field updateUser(@RequestBody Field field){
        return fieldService.updateField(field);
    }

    @PostMapping("/changePhoto/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changePhoto(@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) throws IOException {
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        field.setPhotos(filename);

        try{
            String uploadDir = "/etc/testSpring/field-photos/"+field.getId();
            FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile salvare la foto"));
        }
        fieldService.saveField(field);
        return ResponseEntity.ok("Foto aggiunta correttamente");
    }
    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) throws IOException {
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        if(field.getPhotos()!= null){
            File img = new File("/etc/testSpring/field-photos/" + field.getId() + "/" + field.getPhotos());
            field.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(field);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteField(@PathVariable Long id){
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        fieldRepository.delete(field);
        return ResponseEntity.ok(new MessageResponse("Campo eliminato correttamente"));
    }


}
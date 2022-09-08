package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.repository.FieldRepository;
import com.prenotazionicampo_backend.security.services.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id){
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        return ResponseEntity.ok(field);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteField(@PathVariable Long id){
        Field field = fieldService.findById(id).orElseThrow(()-> new ResourceNotFoundException("Field not found with id: "+id));
        fieldRepository.delete(field);
        Map<String,Boolean> response = new HashMap<>();
        response.put("deleted",Boolean.TRUE);
        return ResponseEntity.ok(response);
    }


}

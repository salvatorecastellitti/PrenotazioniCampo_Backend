package com.prenotazionicampo_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/")
public class HealthController {
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatusServer (){
        Map<String,String> response = new HashMap<>();
        response.put("Status:", "UP");
        return ResponseEntity.ok(response);
    }
}

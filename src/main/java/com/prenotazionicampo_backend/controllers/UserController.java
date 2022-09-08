package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.repository.UserRepository;
import com.prenotazionicampo_backend.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addUser(User user){
        userService.saveUser(user);
        return ResponseEntity.ok("User added correctly, id: "+user.getId());
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id){
        User user = userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: " + id));

        userRepository.deleteById(id);
        Map<String,Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: " + id));
        return ResponseEntity.ok(user);
    }

}

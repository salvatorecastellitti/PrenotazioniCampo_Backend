package com.prenotazionicampo_backend.controllers;
import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.payload.userProfile.ChangePwd;
import com.prenotazionicampo_backend.payload.userProfile.ProfileHolder;
import com.prenotazionicampo_backend.repository.UserRepository;
import com.prenotazionicampo_backend.security.jwt.JwtUtils;
import com.prenotazionicampo_backend.security.services.UserService;
import com.prenotazionicampo_backend.util.FileUploadUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.io.File;

@RestController
@RequestMapping("/api/v1/users/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/list")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/listOther")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getOtherUser(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);

        return userService.findOtherUser(Long.valueOf(id));
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody User user){
        userService.saveUser(user);
        return ResponseEntity.ok(new MessageResponse("Utente aggiunto correttamente: " +user.getId()));
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: " + id));
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Utente eliminato correttamente"));
    }

    //questo non va bene, perche io devo prendere solo i campi mandati da alex, altrimenti perdo i campi con @JsonIgnore
    //https://stackoverflow.com/questions/42693643/updating-user-info-spring-boot
    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> update(@RequestBody ProfileHolder profileHolder,HttpServletRequest request){
        User user;
        if(profileHolder.getId() == null || profileHolder.getId().isEmpty()){
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String id = jwtUtils.getIdNameFromJwtToken(token);
            user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("User not exist with id: " + id));

        }else{
            user = userService.findById(Long.valueOf(profileHolder.getId())).orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + profileHolder.getId()));
        }
        if(profileHolder.getUsername()!=null){
            user.setUsername(profileHolder.getUsername());
        }
        if(profileHolder.getPhone()!=null){
            user.setPhone(profileHolder.getPhone());
        }
        if(profileHolder.getEmail()!=null){
            user.setEmail(profileHolder.getEmail());
        }
        if(profileHolder.getNome()!=null){
            user.setNome(profileHolder.getNome());
        }
        if(profileHolder.getCognome()!=null){
            user.setCognome(profileHolder.getCognome());
        }

        userService.saveUser(user);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/changePhoto")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changeUserPhoto(@RequestParam("image") MultipartFile multipartFile, HttpServletRequest request) throws IOException {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("User not exist with id: " + id));

        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhotos(filename);

        try{
            String uploadDir = "/etc/testSpring/user-photos/"+ user.getId();
            FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile salvare la foto"));
        }

        try{
            userService.updateUser(user);
            return ResponseEntity.ok(new MessageResponse("Utente aggiornato correttamente"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile aggiornare il profilo"));
        }
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePwd changePwd, HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("User not exist with id: " + id));
        if (encoder.matches(changePwd.getOldPassword(),user.getPassword())){

            user.setPassword(encoder.encode(changePwd.getNewPassword()));
        }else{
            return ResponseEntity.badRequest().body(new MessageResponse("La vecchia password non corrisponde a quella attualmente in uso"));
        }

        userService.saveUser(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getAuthUser(HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: " + id));
        if(user.getPhotos() != null){
            File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
            user.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws IOException {
        User user = userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: " + id));
        if (user.getPhotos() != null){
            File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
            user.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(user);
    }

}

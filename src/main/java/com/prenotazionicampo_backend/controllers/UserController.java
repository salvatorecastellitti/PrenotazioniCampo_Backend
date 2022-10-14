package com.prenotazionicampo_backend.controllers;
import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.ERole;
import com.prenotazionicampo_backend.models.Role;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.payload.request.SignupRequest;
import com.prenotazionicampo_backend.payload.response.MessageResponse;
import com.prenotazionicampo_backend.payload.userProfile.ChangePwd;
import com.prenotazionicampo_backend.payload.userProfile.PhotoHolder;
import com.prenotazionicampo_backend.payload.userProfile.ProfileHolder;
import com.prenotazionicampo_backend.repository.RoleRepository;
import com.prenotazionicampo_backend.repository.UserRepository;
import com.prenotazionicampo_backend.security.jwt.JwtUtils;
import com.prenotazionicampo_backend.security.services.UserService;
import com.prenotazionicampo_backend.util.FileUploadUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.util.Set;

@MultipartConfig
@RestController
@RequestMapping("/api/v1/users/")
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/list")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getAllUsers() throws IOException {
        List<User> users = userService.findAll();
        for (User user : users) {
            if (user.getPhotos() != null) {
                File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
                user.setPhotoMedia(FileUtils.readFileToByteArray(img));
            }
        }
        return users;
    }

    @GetMapping("/listOther")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getOtherUser(HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);

        List<User> users = userService.findOtherUser(Long.valueOf(id));;
        for (User user : users) {
            if (user.getPhotos() != null) {
                File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
                user.setPhotoMedia(FileUtils.readFileToByteArray(img));
            }
        }
        return users;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody SignupRequest signUpRequest){
        if (signUpRequest.getName().isEmpty() || signUpRequest.getSurname().isEmpty() || signUpRequest.getEmail().isEmpty() || signUpRequest.getPassword().isEmpty() || signUpRequest.getPhone().isEmpty() || signUpRequest.getUsername().isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponse("Errore! Assicurati che non ci siano campi incompleti",400));
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Errore: Username già in uso!", 400));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Errore: Email già in uso!", 400));
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getPhone());
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Errore: Ruolo non trovato"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Errore: Ruolo non trovato"));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Errore: Ruolo non trovato"));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Errore: Ruolo non trovato"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Utente registrato correttamente",200));

    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("L'utente non esiste con id: " + id));
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Utente eliminato correttamente",200));
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
            user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("L'utente non esiste con id: " + id));

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
        if(profileHolder.getName()!=null){
            user.setName(profileHolder.getName());
        }
        if(profileHolder.getSurname()!=null){
            user.setSurname(profileHolder.getSurname());
        }

        try {
            userService.saveUser(user);
        }catch (Exception e){
            log.error(e);
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile salvare l'utente", 400));
        }

        return ResponseEntity.ok(new MessageResponse("Utente salvato correttamente",200));
    }

    @PostMapping("/changePhoto")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changeUserPhoto(@RequestBody PhotoHolder photoHolder, HttpServletRequest request) throws IOException {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("L'utente non esiste con id: " + id));


        String filename = StringUtils.cleanPath(photoHolder.getImageName());
        user.setPhotos(filename);

        try{
            String photoMedia = photoHolder.getImage().substring(photoHolder.getImage().indexOf(",")+1);
            byte[] imageByte= Base64.getDecoder().decode(photoMedia);
            String uploadDir = "/etc/testSpring/user-photos/"+user.getId()+"/";
            FileUploadUtil.saveFile(uploadDir, filename, imageByte);
        }catch (Exception e){
            log.error(e);
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile salvare la foto",400));
        }

        try{
            userService.saveUser(user);
            return ResponseEntity.ok(new MessageResponse("Utente aggiornato correttamente",200));
        }catch (Exception e){
            log.error(e);
            return ResponseEntity.badRequest().body(new MessageResponse("Impossibile aggiornare il profilo",400));
        }
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePwd changePwd, HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()->new ResourceNotFoundException("L'utente non esiste con id: " + id));
        if (encoder.matches(changePwd.getOldPassword(),user.getPassword())){

            user.setPassword(encoder.encode(changePwd.getNewPassword()));
        }else{
            return ResponseEntity.badRequest().body(new MessageResponse("La vecchia password non corrisponde a quella attualmente in uso",400));
        }

        userService.saveUser(user);
        return ResponseEntity.ok(new MessageResponse("Password aggiornata correttamente!",200));
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getAuthUser(HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String id = jwtUtils.getIdNameFromJwtToken(token);
        User user = userService.findById(Long.valueOf(id)).orElseThrow(()-> new ResourceNotFoundException("L'utente non esiste con id: " + id));
        if(user.getPhotos() != null){
            File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
            user.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws IOException {
        User user = userService.findById(id).orElseThrow(()-> new ResourceNotFoundException("L'utente non esiste con id: " + id));
        if (user.getPhotos() != null){
            File img = new File("/etc/testSpring/user-photos/" + user.getId() + "/" + user.getPhotos());
            user.setPhotoMedia(FileUtils.readFileToByteArray(img));
        }
        return ResponseEntity.ok(user);
    }

}

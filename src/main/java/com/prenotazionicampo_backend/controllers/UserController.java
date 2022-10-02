package com.prenotazionicampo_backend.controllers;

import com.prenotazionicampo_backend.exception.ResourceNotFoundException;
import com.prenotazionicampo_backend.models.User;
import com.prenotazionicampo_backend.repository.UserRepository;
import com.prenotazionicampo_backend.security.jwt.JwtUtils;
import com.prenotazionicampo_backend.security.services.UserService;
import com.prenotazionicampo_backend.util.FileUploadUtil;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.FileTypeMap;
import javax.imageio.IIOException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Optional;

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

    @GetMapping("/list")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/listOther")
    public String getOtherUser(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        return jwtUtils.getIdNameFromJwtToken(token);
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

    @PostMapping("/test/uploadPhoto")
    public ResponseEntity<?> SavePhoto(@RequestParam("image")MultipartFile multipartFile) throws IOException{
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        User user = new User();
        user.setPhotos(filename);
        user.setUsername("photo");
        user.setEmail("photo@mail");
        user.setPhone("3949394949");
        user.setPassword("1234");
        userService.saveUser(user);


        String uploadDir = "/etc/testSpring/user-photos/1";

        FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
        return ResponseEntity.ok("File uploaded successfully.");


    }

    @GetMapping( "/test/display")
    public ResponseEntity<byte[]> testphoto() throws IOException{
        User user = userService.findById(9L).orElseThrow(()-> new ResourceNotFoundException("User not exist with id: "));
        //InputStream in = getClass().getResourceAsStream("/etc/testSpring/user-photos/1" + user.getPhotos());
        //return IOUtils.toByteArray(in);

        File img = new File("/etc/testSpring/user-photos/1/" + user.getPhotos());
        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
    }
}

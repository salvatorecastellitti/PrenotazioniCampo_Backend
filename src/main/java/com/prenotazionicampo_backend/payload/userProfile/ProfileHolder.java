package com.prenotazionicampo_backend.payload.userProfile;

import com.prenotazionicampo_backend.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class ProfileHolder implements Serializable {
    User user;
    MultipartFile multipartFile;
}

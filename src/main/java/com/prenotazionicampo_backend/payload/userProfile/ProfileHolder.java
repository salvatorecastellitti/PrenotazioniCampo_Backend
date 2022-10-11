package com.prenotazionicampo_backend.payload.userProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileHolder {
    String id;
    String username;
    String email;
    String phone;
    String name;
    String surname;
}

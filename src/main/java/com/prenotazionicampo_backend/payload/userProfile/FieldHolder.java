package com.prenotazionicampo_backend.payload.userProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldHolder {
    String id;
    String name;
    String country;
    String address;
}

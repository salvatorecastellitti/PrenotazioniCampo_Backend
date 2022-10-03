package com.prenotazionicampo_backend.payload.userProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePwd {
    String oldPassword;
    String newPassword;
}

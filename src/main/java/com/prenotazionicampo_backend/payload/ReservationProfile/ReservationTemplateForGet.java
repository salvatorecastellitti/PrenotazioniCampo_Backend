package com.prenotazionicampo_backend.payload.ReservationProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationTemplateForGet {
    String startDate;
    String endDate;
    Long userId;
    Long fieldId;
    Boolean showImage;
}

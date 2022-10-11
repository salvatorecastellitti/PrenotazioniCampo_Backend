package com.prenotazionicampo_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Entity
@Table(name = "fields")
@Log4j2
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 20)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String country;
    @NotBlank
    @Size(max = 20)
    private String address;

    @Size(max = 64)
    @JsonIgnore
    private String photos;

    private byte[] photoMedia;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Reservation> reservation;

    public Field(){
    }

    public Field(Long id, String name, String country, String address, Set<Reservation> reservation) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.address = address;
        this.reservation = reservation;
    }

    public Field(Long id, String name, String country, String address, String photos, byte[] photoMedia) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.address = address;
        this.photos = photos;
        this.photoMedia = photoMedia;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public byte[] getPhotoMedia() {
        return photoMedia;
    }

    public void setPhotoMedia(byte[] photoMedia) {
        this.photoMedia = photoMedia;
    }

    public Set<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(Set<Reservation> reservation) {
        this.reservation = reservation;
    }
}

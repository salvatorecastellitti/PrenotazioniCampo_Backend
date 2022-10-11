package com.prenotazionicampo_backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @NotBlank
    private Date startDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @NotBlank
    private Date endDate;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    @NotNull
    private User user;


    @ManyToOne(targetEntity = Field.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "field")
    @NotNull
    private Field field;


    public Reservation() {
    }

    public Reservation(Long id, Date startDate, Date endDate, User user, Field field) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.field = field;
    }

    public Reservation(Date startDate, Date endDate, User user, Field field) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.field = field;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

}

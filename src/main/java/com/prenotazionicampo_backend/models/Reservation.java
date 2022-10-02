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
    @JoinColumn(name = "user", insertable = false, updatable = false)
    @JsonIgnore
    @NotNull
    private User user;

    @Column(name = "user")
    private Long userId;

    @ManyToOne(targetEntity = Field.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "field", insertable = false, updatable = false)
    @NotNull
    @JsonIgnore
    private Field field;

    @Column(name = "field")
    private Long fieldId;

    public Reservation() {
    }

    public Reservation(Long id, Date startDate, Date endDate, User user, Field field) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.field = field;
    }

    public Reservation(Long id, Date startDate, Date endDate, User user, Long userId, Field field, Long fieldId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.userId = userId;
        this.field = field;
        this.fieldId = fieldId;
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

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

package com.prenotazionicampo_backend.payload.request;

import java.util.Set;

import javax.validation.constraints.*;

public class SignupRequest {
    @NotBlank
    @NotNull
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @NotNull
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @NotNull
    private Set<String> role;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 20)
    private String name;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 20)
    private String surname;

    @NotBlank
    @NotNull
    @Size(min = 10, max = 15)
    private String phone;

    @NotBlank
    @NotNull
    @Size(min = 6, max = 40)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
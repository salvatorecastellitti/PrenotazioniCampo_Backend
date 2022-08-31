package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    User updateUser(User user);



}

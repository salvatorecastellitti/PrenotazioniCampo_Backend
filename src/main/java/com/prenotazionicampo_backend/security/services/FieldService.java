package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Field;

import java.util.List;
import java.util.Optional;

public interface FieldService {
    Field saveField(Field field);

    List<Field> findAll();

    Optional<Field> findById(Long id);

    Field updateField(Field field);
}

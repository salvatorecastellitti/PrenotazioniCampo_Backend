package com.prenotazionicampo_backend.security.services;

import com.prenotazionicampo_backend.models.Field;
import com.prenotazionicampo_backend.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FieldServiceImpl implements FieldService{

    @Autowired
    private FieldRepository fieldRepository;

    @Override
    public Field saveField(Field field) {
        return fieldRepository.save(field);
    }

    @Override
    public List<Field> findAll() {
        return fieldRepository.findAll();
    }

    @Override
    public Optional<Field> findById(Long id) {
        return fieldRepository.findById(id);
    }

    @Override
    public Field updateField(Field field) {
        return fieldRepository.save(field);
    }
}

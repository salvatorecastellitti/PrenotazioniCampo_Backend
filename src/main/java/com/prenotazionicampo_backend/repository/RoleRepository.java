package com.prenotazionicampo_backend.repository;

import com.prenotazionicampo_backend.models.ERole;
import com.prenotazionicampo_backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}

package com.prenotazionicampo_backend.repository;

import com.prenotazionicampo_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername (String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String username);

    @Query(value = "SELECT * FROM users where id <> :id", nativeQuery = true)
    List<User> findOtherUser(Long id);
}

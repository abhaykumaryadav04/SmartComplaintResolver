package com.smartcomplaint.smartcompaint.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByRole(Role role);
}

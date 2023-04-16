package com.example.SpringBootDemoApplication.repositories.auth;

import com.example.SpringBootDemoApplication.models.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
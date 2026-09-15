package com.moura.agrios.repositories;
import com.moura.agrios.models.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<Users, String> {
    
    UserDetails findByUsername(String username);
}
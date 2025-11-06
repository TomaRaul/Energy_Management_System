package com.ds.ems.repositories;

import com.ds.ems.entities.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
    
    Optional<Credentials> findByUsername(String username);
    
    boolean existsByUsername(String username);
}

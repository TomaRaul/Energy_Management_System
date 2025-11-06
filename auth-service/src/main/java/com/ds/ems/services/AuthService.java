package com.ds.ems.services;

import com.ds.ems.dtos.LoginRequest;
import com.ds.ems.dtos.LoginResponse;
import com.ds.ems.entities.Credentials;
import com.ds.ems.repositories.CredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final CredentialsRepository credentialsRepository;

    @Autowired
    public AuthService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LOGGER.info("Login attempt for username: {}", loginRequest.getUsername());
        
        Optional<Credentials> credentialsOpt = credentialsRepository.findByUsername(loginRequest.getUsername());
        
        if (!credentialsOpt.isPresent()) {
            LOGGER.warn("User not found: {}", loginRequest.getUsername());
            return new LoginResponse(null, null, false);
        }
        
        Credentials credentials = credentialsOpt.get();

//        long nowMillis = System.currentTimeMillis();
//        Date now = new Date(nowMillis);
//        Date expireDate = new Date(nowMillis);
//        Key key = MacProvider.generateKey();
//        String compactJws = Jwts.builder()
//                .setSubject(name)
//                .setAudience("users")
//                .setIssuedAt(now)
//                .setExpiration(expireDate)
//                .signWith(SignatureAlgorithm.HS512, key)
//                .signWith(S)
//                .compact();

        if (credentials.getPassword().equals(loginRequest.getPassword())) {
            LOGGER.info("Login successful for user: {}", loginRequest.getUsername());
            return new LoginResponse("jwt_token_" + credentials.getUsername(), credentials.getUsername(), true);
        } else {
            LOGGER.warn("Invalid password for user: {}", loginRequest.getUsername());
            return new LoginResponse(null, null, false);
        }
    }

    public LoginResponse register(LoginRequest registerRequest) {
        LOGGER.info("Register attempt for username: {}", registerRequest.getUsername());
        
        if (credentialsRepository.existsByUsername(registerRequest.getUsername())) {
            LOGGER.warn("Username already exists: {}", registerRequest.getUsername());
            return new LoginResponse(null, null, false);
        }
        
        Credentials credentials = new Credentials();
        credentials.setUsername(registerRequest.getUsername());
        credentials.setPassword(registerRequest.getPassword());
        
        credentialsRepository.save(credentials);
        
        LOGGER.info("User registered successfully: {}", registerRequest.getUsername());
        return new LoginResponse("jwt_token_" + credentials.getUsername(), credentials.getUsername(), true);
    }
}

package com.ds.ems.services;

import com.ds.ems.dtos.*;
import com.ds.ems.entities.Credentials;
import com.ds.ems.repositories.CredentialsRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Chars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.security.Key;
import java.util.stream.Collectors;

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

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_"+credentials.getRole().toUpperCase()));

        // construiește token-ul
        String jwt = Jwts.builder()
                .setClaims(claims) // adauga claim-ul cu roluri
                .setSubject(credentials.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 ora
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();


        if (credentials.getPassword().equals(loginRequest.getPassword())) {
            LOGGER.info("Login successful for user: {}", loginRequest.getUsername());
            return new LoginResponse(jwt, credentials.getUsername(), true);
        } else {
            LOGGER.warn("Invalid password for user: {}", loginRequest.getUsername());
            return new LoginResponse(null, null, false);
        }
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        LOGGER.info("Register attempt for username: {}", registerRequest.getUsername());

        if (credentialsRepository.existsByUsername(registerRequest.getUsername())) {
            LOGGER.warn("Username already exists: {}", registerRequest.getUsername());
            return new RegisterResponse( null, false);
        }

        Credentials credentials = new Credentials();
        credentials.setId(registerRequest.getId());
        credentials.setUsername(registerRequest.getUsername());
        credentials.setPassword(registerRequest.getPassword());
        credentials.setRole(registerRequest.getRole());
        credentialsRepository.save(credentials);

        LOGGER.info("User registered successfully: {}", registerRequest.getUsername());
        return new RegisterResponse(credentials.getUsername(), true);
    }

    public List<UsersDTO> getUsers() {
        LOGGER.info("GetUsers attempt");
        List<Credentials> UsersList = credentialsRepository.findAll();

        LOGGER.info("GetUsers successfully");
        return UsersList.stream()
                .map(name -> new UsersDTO(name.getId(), name.getUsername(), name.getRole()))
                .collect(Collectors.toList());
    }

    public void deleteUser(int id) {
        LOGGER.info("Deleting user with ID: {}", id);

        // verifica ca user-ul exista
        if (!credentialsRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // sterge user-ul
        credentialsRepository.deleteById(id);
        LOGGER.info("User deleted successfully: {}", id);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("K7A24qesyls5nKqOqYv6L+N8D5C4w3A6E9B3zF0pG2rV7uXzR8iY/J6kZ3sV5bY+P9C7gA3bE6fH0jD5eS8gQ==");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package com.ds.ems.controllers;

import com.ds.ems.dtos.UsersDTO;
import com.ds.ems.dtos.UsersDetailsDTO;
import com.ds.ems.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UsersController {

    private final UsersService UsersService;

    public UsersController(UsersService usersService) {
        this.UsersService = usersService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<UsersDTO>> getUsers() {
        return ResponseEntity.ok(UsersService.findUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsersDetailsDTO> getUsers(@PathVariable int id) {
        return ResponseEntity.ok(UsersService.findUsersById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody UsersDetailsDTO Users) {
        int id = UsersService.insert(Users);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        UsersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UsersDetailsDTO dto) {
        UsersService.updateUser(id, dto);
        return ResponseEntity.ok().build();
    }
}

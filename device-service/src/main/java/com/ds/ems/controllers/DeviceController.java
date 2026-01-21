package com.ds.ems.controllers;

import com.ds.ems.dtos.DeviceDTO;
import com.ds.ems.dtos.DeviceDetailsDTO;
import com.ds.ems.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/devices")
@Validated
public class DeviceController {

    private final DeviceService DeviceService;

    public DeviceController(DeviceService DeviceService) {
        this.DeviceService = DeviceService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceDTO>> getDevice() {
        return ResponseEntity.ok(DeviceService.findDevice());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable int id) {
        return ResponseEntity.ok(DeviceService.findDeviceById(id));
    }

    @GetMapping("/my/{user_id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<DeviceDTO>> getDeviceByUserId(@PathVariable int user_id) {
        return ResponseEntity.ok(DeviceService.findDeviceByUserId(user_id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO Device) {
        int id = DeviceService.insert(Device);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable int id) {
        DeviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateDevice(
            @PathVariable Integer id,
            @Valid @RequestBody DeviceDetailsDTO dto) {
        DeviceService.updateDevice(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/intern/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDeviceInternal(@PathVariable int id) {
        return ResponseEntity.ok(DeviceService.findDeviceById(id));
    }
}

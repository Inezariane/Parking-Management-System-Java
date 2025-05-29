package com.parking.controller;

import com.parking.dto.ApiResponse;
import com.parking.dto.VehicleRequest;
import com.parking.entity.Vehicle;
import com.parking.service.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Vehicle>> addVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setType(request.getType());
        vehicle.setSize(request.getSize());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        
        Vehicle saved = vehicleService.addVehicle(userDetails.getUsername(), vehicle);
        return ResponseEntity.ok(ApiResponse.success("Vehicle added successfully", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setType(request.getType());
        vehicle.setSize(request.getSize());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        
        Vehicle updated = vehicleService.updateVehicle(id, vehicle);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<Vehicle>>> listVehicles(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehicles = vehicleService.listVehicles(userDetails.getUsername(), search, pageable);
        return ResponseEntity.ok(ApiResponse.success(vehicles));
    }
} 
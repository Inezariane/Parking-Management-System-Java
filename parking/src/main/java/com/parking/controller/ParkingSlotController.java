package com.parking.controller;

import com.parking.dto.ApiResponse;
import com.parking.dto.ParkingSlotRequest;
import com.parking.dto.BulkParkingSlotRequest;
import com.parking.entity.ParkingSlot;
import com.parking.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Tag(name = "Parking Slot Management", description = "APIs for managing parking slots")
@SecurityRequirement(name = "bearerAuth")
public class ParkingSlotController {
    private final ParkingSlotService parkingSlotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParkingSlot>> addSlot(@Valid @RequestBody ParkingSlotRequest request) {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(request.getSlotNumber());
        slot.setSize(request.getSize());
        slot.setVehicleType(request.getVehicleType());
        slot.setLocation(request.getLocation());
        slot.setAvailable(true);
        
        ParkingSlot saved = parkingSlotService.addSlot(slot);
        return ResponseEntity.ok(ApiResponse.success("Parking slot added successfully", saved));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> addSlotsBulk(@Valid @RequestBody BulkParkingSlotRequest request) {
        List<ParkingSlot> slots = request.getSlots().stream()
            .map(slotRequest -> {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber(slotRequest.getSlotNumber());
                slot.setSize(slotRequest.getSize());
                slot.setVehicleType(slotRequest.getVehicleType());
                slot.setLocation(slotRequest.getLocation());
                slot.setAvailable(true);
                return slot;
            })
            .toList();
        
        List<ParkingSlot> saved = parkingSlotService.addSlotsBulk(slots);
        return ResponseEntity.ok(ApiResponse.success("Parking slots added successfully", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParkingSlot>> updateSlot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingSlotRequest request) {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(request.getSlotNumber());
        slot.setSize(request.getSize());
        slot.setVehicleType(request.getVehicleType());
        slot.setLocation(request.getLocation());
        
        ParkingSlot updated = parkingSlotService.updateSlot(id, slot);
        return ResponseEntity.ok(ApiResponse.success("Parking slot updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long id) {
        parkingSlotService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponse.success("Parking slot deleted successfully", null));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ParkingSlot>>> listSlots(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "slotNumber") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<ParkingSlot> slots = parkingSlotService.listSlots(search, available, pageable);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }
} 
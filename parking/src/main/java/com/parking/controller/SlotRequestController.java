package com.parking.controller;

import com.parking.dto.ApiResponse;
import com.parking.entity.SlotRequest;
import com.parking.service.SlotRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slot-requests")
@RequiredArgsConstructor
@Tag(name = "Slot Request Management", description = "APIs for managing parking slot requests")
@SecurityRequirement(name = "bearerAuth")
public class SlotRequestController {

    private final SlotRequestService slotRequestService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<SlotRequest>> createRequest(
            @RequestParam Long userId,
            @RequestParam Long vehicleId) {
        SlotRequest created = slotRequestService.createRequest(userId, vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Slot request created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlotRequest>> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody SlotRequest request) {
        SlotRequest updated = slotRequestService.updateRequest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Slot request updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        slotRequestService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Slot request deleted successfully", null));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlotRequest>> approveRequest(@PathVariable Long id) {
        SlotRequest approved = slotRequestService.approveRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Slot request approved successfully", approved));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlotRequest>> rejectRequest(
            @PathVariable Long id,
            @RequestParam String reason) {
        SlotRequest rejected = slotRequestService.rejectRequest(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Slot request rejected successfully", rejected));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Page<SlotRequest>>> listRequests(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<SlotRequest> requests = slotRequestService.listRequests(userId, status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
}

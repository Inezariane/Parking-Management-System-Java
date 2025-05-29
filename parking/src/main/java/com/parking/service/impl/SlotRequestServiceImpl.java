package com.parking.service.impl;

import com.parking.entity.*;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.SlotRequestRepository;
import com.parking.repository.VehicleRepository;
import com.parking.service.AuditLogService;
import com.parking.service.EmailService;
import com.parking.service.SlotRequestService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SlotRequestServiceImpl implements SlotRequestService {
    private final SlotRequestRepository slotRequestRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    @Override
    @Transactional
    public SlotRequest createRequest(Long userId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        if (slotRequestRepository.existsByUserIdAndStatus(userId, RequestStatus.PENDING)) {
            throw new IllegalStateException("User already has a pending request");
        }

        SlotRequest request = new SlotRequest();
        request.setUser(vehicle.getUser());
        request.setVehicle(vehicle);
        request.setStatus(RequestStatus.PENDING);

        SlotRequest saved = slotRequestRepository.save(request);
        
        auditLogService.logAction(userId, "CREATE_SLOT_REQUEST", 
            "Created slot request for vehicle: " + vehicle.getPlateNumber(), null);
        
        return saved;
    }

    @Override
    @Transactional
    public SlotRequest updateRequest(Long id, SlotRequest request) {
        SlotRequest existing = slotRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (existing.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only update pending requests");
        }

        existing.setStatus(request.getStatus());
        SlotRequest updated = slotRequestRepository.save(existing);
        
        auditLogService.logAction(existing.getUser().getId(), "UPDATE_SLOT_REQUEST", 
            "Updated slot request status to: " + request.getStatus(), null);
        
        return updated;
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        SlotRequest request = slotRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only delete pending requests");
        }

        Long userId = request.getUser().getId();
        String vehiclePlate = request.getVehicle().getPlateNumber();
        
        slotRequestRepository.delete(request);
        
        auditLogService.logAction(userId, "DELETE_SLOT_REQUEST", 
            "Deleted slot request for vehicle: " + vehiclePlate, null);
    }

    @Override
    @Transactional
    public SlotRequest approveRequest(Long id) {
        SlotRequest request = slotRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only approve pending requests");
        }

        Optional<ParkingSlot> slot = findCompatibleSlot(request.getVehicle());
        if (slot.isEmpty()) {
            throw new IllegalStateException("No compatible parking slot available");
        }

        ParkingSlot parkingSlot = slot.get();
        parkingSlot.setAvailable(false);
        parkingSlot.setCurrentVehicle(request.getVehicle());
        parkingSlotRepository.save(parkingSlot);

        request.setStatus(RequestStatus.APPROVED);
        request.setParkingSlot(parkingSlot);
        SlotRequest approved = slotRequestRepository.save(request);

        emailService.sendSlotApprovalEmail(request.getUser().getEmail(), parkingSlot.getSlotNumber(),
            request.getVehicle().getPlateNumber(), parkingSlot.getLocation());
        
        auditLogService.logAction(request.getUser().getId(), "APPROVE_SLOT_REQUEST", 
            "Approved slot request for vehicle: " + request.getVehicle().getPlateNumber(), null);
        
        return approved;
    }

    @Override
    @Transactional
    public SlotRequest rejectRequest(Long id, String reason) {
        SlotRequest request = slotRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only reject pending requests");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        SlotRequest rejected = slotRequestRepository.save(request);

        emailService.sendSlotRejectionEmail(request.getUser().getEmail(), reason);
        
        auditLogService.logAction(request.getUser().getId(), "REJECT_SLOT_REQUEST", 
            "Rejected slot request for vehicle: " + request.getVehicle().getPlateNumber(), reason);
        
        return rejected;
    }

    @Override
    public Page<SlotRequest> listRequests(Long userId, String status, String search, Pageable pageable) {
        Specification<SlotRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), RequestStatus.valueOf(status.toUpperCase())));
            }
            
            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("vehicle").get("plateNumber")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("parkingSlot").get("slotNumber")), "%" + search.toLowerCase() + "%")
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return slotRequestRepository.findAll(spec, pageable);
    }

    private Optional<ParkingSlot> findCompatibleSlot(Vehicle vehicle) {
        Specification<ParkingSlot> spec = (root, query, cb) -> 
            cb.and(
                cb.isTrue(root.get("available")),
                cb.equal(root.get("vehicleType"), vehicle.getType()),
                cb.equal(root.get("size"), vehicle.getSize())
            );
            
        return parkingSlotRepository.findAll(spec).stream().findFirst();
    }
}
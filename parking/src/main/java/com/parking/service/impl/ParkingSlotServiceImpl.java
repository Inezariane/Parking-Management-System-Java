package com.parking.service.impl;

import com.parking.entity.ParkingSlot;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.ParkingSlotService;
import com.parking.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ParkingSlot addSlot(ParkingSlot slot) {
        ParkingSlot saved = parkingSlotRepository.save(slot);
        auditLogService.logAction(null, "ADD_SLOT", 
            "Added parking slot: " + slot.getSlotNumber(), null);
        return saved;
    }

    @Override
    @Transactional
    public List<ParkingSlot> addSlotsBulk(List<ParkingSlot> slots) {
        List<ParkingSlot> saved = parkingSlotRepository.saveAll(slots);
        auditLogService.logAction(null, "ADD_SLOTS_BULK", 
            "Added " + slots.size() + " parking slots", null);
        return saved;
    }

    @Override
    @Transactional
    public ParkingSlot updateSlot(Long id, ParkingSlot slot) {
        ParkingSlot existing = parkingSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parking slot not found"));
        
        existing.setSlotNumber(slot.getSlotNumber());
        existing.setSize(slot.getSize());
        existing.setVehicleType(slot.getVehicleType());
        existing.setLocation(slot.getLocation());
        
        ParkingSlot updated = parkingSlotRepository.save(existing);
        auditLogService.logAction(null, "UPDATE_SLOT", 
            "Updated parking slot: " + slot.getSlotNumber(), null);
        return updated;
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) {
        ParkingSlot slot = parkingSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parking slot not found"));
        
        String slotNumber = slot.getSlotNumber();
        parkingSlotRepository.delete(slot);
        
        auditLogService.logAction(null, "DELETE_SLOT", 
            "Deleted parking slot: " + slotNumber, null);
    }

    @Override
    public Page<ParkingSlot> listSlots(String search, Boolean available, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (available != null) {
                return parkingSlotRepository.findBySlotNumberContainingIgnoreCaseAndAvailable(search, available, pageable);
            }
            return parkingSlotRepository.findBySlotNumberContainingIgnoreCase(search, pageable);
        }
        if (available != null) {
            return parkingSlotRepository.findByAvailable(available, pageable);
        }
        return parkingSlotRepository.findAll(pageable);
    }
} 
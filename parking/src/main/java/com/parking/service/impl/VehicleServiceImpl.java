package com.parking.service.impl;

import com.parking.entity.User;
import com.parking.entity.Vehicle;
import com.parking.repository.UserRepository;
import com.parking.repository.VehicleRepository;
import com.parking.service.AuditLogService;
import com.parking.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Vehicle addVehicle(String userEmail, Vehicle vehicle) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        vehicle.setUser(user);
        Vehicle saved = vehicleRepository.save(vehicle);
        
        auditLogService.logAction(user.getId(), "ADD_VEHICLE", 
            "Added vehicle with plate number: " + vehicle.getPlateNumber(), null);
        
        return saved;
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        
        existingVehicle.setPlateNumber(vehicle.getPlateNumber());
        existingVehicle.setType(vehicle.getType());
        existingVehicle.setSize(vehicle.getSize());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setColor(vehicle.getColor());
        
        Vehicle updated = vehicleRepository.save(existingVehicle);
        
        auditLogService.logAction(existingVehicle.getUser().getId(), "UPDATE_VEHICLE", 
            "Updated vehicle with plate number: " + vehicle.getPlateNumber(), null);
        
        return updated;
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        Long userId = vehicle.getUser().getId();
        String plateNumber = vehicle.getPlateNumber();
        
        vehicleRepository.delete(vehicle);
        
        auditLogService.logAction(userId, "DELETE_VEHICLE", 
            "Deleted vehicle with plate number: " + plateNumber, null);
    }

    @Override
    public Page<Vehicle> listVehicles(String userEmail, String search, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (search != null && !search.trim().isEmpty()) {
            // Try searching by plate number first
            Page<Vehicle> plateNumberResults = vehicleRepository.findByUserAndPlateNumberContainingIgnoreCase(user, search, pageable);
            if (!plateNumberResults.isEmpty()) {
                return plateNumberResults;
            }
            
            // Try searching by model
            Page<Vehicle> modelResults = vehicleRepository.findByUserAndModelContainingIgnoreCase(user, search, pageable);
            if (!modelResults.isEmpty()) {
                return modelResults;
            }
            
            // Try searching by color
            return vehicleRepository.findByUserAndColorContainingIgnoreCase(user, search, pageable);
        }
        
        // If no search term, return all vehicles for the user with pagination and sorting
        return vehicleRepository.findByUser(user, pageable);
    }

    @Override
    public Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
    }
} 
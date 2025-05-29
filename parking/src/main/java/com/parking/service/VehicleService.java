package com.parking.service;

import com.parking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface VehicleService {
    Vehicle addVehicle(String userEmail, Vehicle vehicle);
    Vehicle updateVehicle(Long id, Vehicle vehicle);
    void deleteVehicle(Long id);
    Page<Vehicle> listVehicles(String userEmail, String search, Pageable pageable);
    Vehicle getVehicle(Long id);
} 
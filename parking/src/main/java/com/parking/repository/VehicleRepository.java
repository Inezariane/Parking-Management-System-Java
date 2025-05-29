package com.parking.repository;

import com.parking.entity.User;
import com.parking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    List<Vehicle> findByUserId(Long userId);
    boolean existsByPlateNumber(String plateNumber);
    Page<Vehicle> findByUser(User user, Pageable pageable);
    Page<Vehicle> findByUserAndPlateNumberContainingIgnoreCase(User user, String plateNumber, Pageable pageable);
    Page<Vehicle> findByUserAndModelContainingIgnoreCase(User user, String model, Pageable pageable);
    Page<Vehicle> findByUserAndColorContainingIgnoreCase(User user, String color, Pageable pageable);
} 
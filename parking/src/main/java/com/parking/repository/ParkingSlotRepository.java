package com.parking.repository;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.entity.VehicleSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long>, JpaSpecificationExecutor<ParkingSlot> {
    List<ParkingSlot> findByAvailable(boolean available);
    List<ParkingSlot> findByVehicleTypeAndSize(VehicleType type, VehicleSize size);
    boolean existsBySlotNumber(String slotNumber);
    Page<ParkingSlot> findBySlotNumberContainingIgnoreCase(String slotNumber, Pageable pageable);
    Page<ParkingSlot> findByAvailable(Boolean available, Pageable pageable);
    Page<ParkingSlot> findBySlotNumberContainingIgnoreCaseAndAvailable(String slotNumber, Boolean available, Pageable pageable);
} 
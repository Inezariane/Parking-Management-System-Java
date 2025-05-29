package com.parking.service;

import com.parking.entity.ParkingSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingSlotService {
    ParkingSlot addSlot(ParkingSlot slot);
    List<ParkingSlot> addSlotsBulk(List<ParkingSlot> slots);
    ParkingSlot updateSlot(Long id, ParkingSlot slot);
    void deleteSlot(Long id);
    Page<ParkingSlot> listSlots(String search, Boolean available, Pageable pageable);
} 
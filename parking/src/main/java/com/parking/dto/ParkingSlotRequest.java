package com.parking.dto;

import com.parking.entity.VehicleSize;
import com.parking.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSlotRequest {
    @NotBlank
    private String slotNumber;

    @NotNull
    private VehicleSize size;

    @NotNull
    private VehicleType vehicleType;

    @NotBlank
    private String location;
} 
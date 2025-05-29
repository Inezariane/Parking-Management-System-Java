package com.parking.dto;

import com.parking.entity.VehicleSize;
import com.parking.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {
    @NotBlank
    private String plateNumber;

    @NotNull
    private VehicleType type;

    @NotNull
    private VehicleSize size;

    @NotBlank
    private String model;

    @NotBlank
    private String color;
} 
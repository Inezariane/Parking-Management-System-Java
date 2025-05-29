package com.parking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkParkingSlotRequest {
    @NotEmpty
    @Valid
    private List<ParkingSlotRequest> slots;
}
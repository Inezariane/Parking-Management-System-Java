package com.parking.service;

import com.parking.entity.SlotRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SlotRequestService {
    SlotRequest createRequest(Long userId, Long vehicleId);
    SlotRequest updateRequest(Long id, SlotRequest request);
    void deleteRequest(Long id);
    SlotRequest approveRequest(Long id);
    SlotRequest rejectRequest(Long id, String reason);
    Page<SlotRequest> listRequests(Long userId, String status, String search, Pageable pageable);
}
 
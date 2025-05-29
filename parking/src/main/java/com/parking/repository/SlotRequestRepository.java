package com.parking.repository;

import com.parking.entity.SlotRequest;
import com.parking.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRequestRepository extends JpaRepository<SlotRequest, Long>, JpaSpecificationExecutor<SlotRequest> {
    List<SlotRequest> findByUserId(Long userId);
    List<SlotRequest> findByStatus(RequestStatus status);
    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);
} 
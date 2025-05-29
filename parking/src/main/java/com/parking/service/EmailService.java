package com.parking.service;

public interface EmailService {
    void sendSlotApprovalEmail(String to, String slotNumber, String vehiclePlate, String location);
    void sendSlotRejectionEmail(String to, String reason);
    void sendPasswordResetEmail(String to, String resetToken);
} 
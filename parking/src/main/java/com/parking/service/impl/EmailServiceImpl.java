package com.parking.service.impl;

import com.parking.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendSlotApprovalEmail(String to, String slotNumber, String vehiclePlate, String location) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Parking Slot Approved");
        message.setText(String.format(
            "Your parking slot request has been approved!\n\n" +
            "Slot Number: %s\n" +
            "Vehicle Plate: %s\n" +
            "Location: %s\n\n" +
            "Thank you for using our parking service.",
            slotNumber, vehiclePlate, location
        ));
        mailSender.send(message);
    }

    @Override
    public void sendSlotRejectionEmail(String to, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Parking Slot Request Rejected");
        message.setText(String.format(
            "Your parking slot request has been rejected.\n\n" +
            "Reason: %s\n\n" +
            "Please contact the parking management for more information.",
            reason
        ));
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText(String.format(
            "You have requested to reset your password.\n\n" +
            "Reset Token: %s\n\n" +
            "If you did not request this, please ignore this email.",
            resetToken
        ));
        mailSender.send(message);
    }
} 
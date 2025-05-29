package com.parking.service;

import com.parking.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    User register(User user);
    Optional<User> findByEmail(String email);
    User updateProfile(Long userId, User updatedUser);
    void changePassword(Long userId, String newPassword);
} 
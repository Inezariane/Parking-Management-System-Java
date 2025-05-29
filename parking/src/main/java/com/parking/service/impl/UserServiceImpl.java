package com.parking.service.impl;

import com.parking.entity.User;
import com.parking.repository.UserRepository;
import com.parking.service.UserService;
import com.parking.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        auditLogService.logAction(saved.getId(), "REGISTER", "User registered", null);
        return saved;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        User saved = userRepository.save(user);
        auditLogService.logAction(userId, "UPDATE_PROFILE", "User updated profile", null);
        return saved;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogService.logAction(userId, "CHANGE_PASSWORD", "User changed password", null);
    }
} 
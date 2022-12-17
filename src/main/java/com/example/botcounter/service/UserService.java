package com.example.botcounter.service;

import com.example.botcounter.entity.User;
import com.example.botcounter.repo.UserRepo;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(@Autowired UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isExist(Long userId) {
        return userRepo.existsById(userId);
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new NotFoundException(
                "User was not found by user_id: "));
    }

    public Double getUserLimitPerDayById(Long userId) {
        return getUserById(userId).getLimitPerDay();
    }

    public void setUserLimitPerDayById(Long userId, Double limitPerDay) {
        User user = getUserById(userId);
        user.setLimitPeDay(limitPerDay);
        saveUser(user);
    }

    public void saveUser(User user) {
        userRepo.save(user);
    }

    public void deleteUser(Long userId) {
        userRepo.deleteById(userId);
    }
}

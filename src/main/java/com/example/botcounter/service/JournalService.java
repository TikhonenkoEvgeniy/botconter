package com.example.botcounter.service;

import com.example.botcounter.entity.Journal;
import com.example.botcounter.repo.JournalRepo;
import com.example.botcounter.util.MyCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JournalService {
    private final JournalRepo journalRepo;

    public JournalService(@Autowired JournalRepo journalRepo) {
        this.journalRepo = journalRepo;
    }

    public void deleteByUserId(Long userId) {
        journalRepo.removeAllByUserId(userId);
    }

    public boolean isExistByUserId(Long userId) {
        return journalRepo.existsByUserIdAndDate(userId, MyCalendar.getCurrentDate());
    }

    public Double getBalanceOnToday(Long userId) {
        return journalRepo.findByUserIdAndDate(userId, MyCalendar.getCurrentDate()).getBalance();
    }

    public void saveBalanceOnToday(Long userId, Double balance) {
        Journal journal;
        if (isExistByUserId(userId)) {
            journal = journalRepo.findByUserIdAndDate(userId, MyCalendar.getCurrentDate());
            journal.setBalance(balance);
        } else {
            journal = new Journal(userId, MyCalendar.getCurrentDate(), balance);
        }
        journalRepo.save(journal);
    }

    public Long getSumBalanceByUserId(Long userId) {
        return journalRepo.sumBalanceByUserId(userId);
    }

    public int countAllByUserId(Long userId) {
        return journalRepo.countAllByUserId(userId);
    }

    public String getLastDateByUserId(Long userId) {
        return journalRepo.findFirstByUserIdOrderByDateDesc(userId).getDate();
    }
}

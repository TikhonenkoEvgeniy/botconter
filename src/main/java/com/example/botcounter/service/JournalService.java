package com.example.botcounter.service;

import com.example.botcounter.entity.Journal;
import com.example.botcounter.repo.JournalRepo;
import com.example.botcounter.util.MyCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Service
public class JournalService {
    private final JournalRepo journalRepo;

    public JournalService(@Autowired JournalRepo journalRepo) {
        this.journalRepo = journalRepo;
    }

    public void deleteByUserId(Long userId) {
        // todo не работает удаление
        //journalRepo.removeAllByUserId(userId);
    }

    public boolean isExistByUserId(Long userId) {
        return journalRepo.existsByUserIdAndDate(userId, MyCalendar.getCurrentDate());
    }

    public Double getBalanceOnToday(Long userId) {
        return journalRepo.findByUserIdAndDate(userId, MyCalendar.getCurrentDate()).getBalance();
    }

    public void saveBalanceOnToday(Long userId, Double balance) {
        saveBalanceOnToday(userId, balance, MyCalendar.getCurrentDate());
    }

    public void saveBalanceOnToday(Long userId, Double balance, String date) {
        Journal journal;
        if (isExistByUserId(userId)) {
            journal = journalRepo.findByUserIdAndDate(userId, date);
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

    public boolean checkLastDateIsToday(Long userId) {
        return journalRepo.findFirstByUserIdOrderByDateDesc(userId).getDate().equals(MyCalendar.getCurrentDate());
    }

    public void fillAllEmptyDaysByUserId(Long userId, double limit) {
        Set<String> allEmptyDates = MyCalendar.getAllDates(getLastDateByUserId(userId));
        if (!allEmptyDates.isEmpty()) {
            for (String date : allEmptyDates) {
                saveBalanceOnToday(userId, limit, date);
            }
        }
    }
}

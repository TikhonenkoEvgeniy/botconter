package com.example.botcounter.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "journals")
public class Journal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "date")
    private String date;
    @Column(name = "balance")
    private Double balance;

    public Journal() {
    }

    public Journal(Long userId, String date, Double balance) {
        this.userId = userId;
        this.date = date;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", balance=" + balance + '}';
    }
}

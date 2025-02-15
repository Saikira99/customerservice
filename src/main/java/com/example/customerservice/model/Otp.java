package com.example.customerservice.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Recipient's email address
    private String recipient;

    // The OTP code
    private String code;

    // OTP expiration time
    private LocalDateTime expiryTime;

    public Otp() {}

    public Otp(String recipient, String code, LocalDateTime expiryTime) {
        this.recipient = recipient;
        this.code = code;
        this.expiryTime = expiryTime;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
}

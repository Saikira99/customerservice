package com.example.customerservice.service;

import com.example.customerservice.model.Otp;
import com.example.customerservice.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender; // For sending OTP via email

    // Generate a random 6-digit OTP
    public String generateOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    // Send OTP via email
    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + ". It is valid for 5 minutes.");
        mailSender.send(message);
    }

    // Create and send an OTP to the given recipient (email)
    public void createAndSendOtp(String recipient, boolean isEmail) {
        // In this simplified scenario, we assume isEmail is always true
        String otpCode = generateOtp();
        Otp otp = new Otp(recipient, otpCode, LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);
        sendOtpEmail(recipient, otpCode);
    }

    // Verify the OTP; if valid, delete it and return true; otherwise, return false.
    public boolean verifyOtp(String recipient, String otpCode) {
        Optional<Otp> otpOpt = otpRepository.findByRecipientAndCode(recipient, otpCode);
        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            if (otp.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otp);
                return true;
            } else {
                otpRepository.delete(otp);
                return false;
            }
        }
        return false;
    }
}

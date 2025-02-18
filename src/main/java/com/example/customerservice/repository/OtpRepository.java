package com.example.customerservice.repository;

import com.example.customerservice.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByRecipientAndCode(String recipient, String code);
}

package com.example.customerservice.controller;

import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.OtpService;
import com.example.customerservice.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 1. Register a new customer using email
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        // Check if email is already registered
        if (customer.getEmail() != null && customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered.");
        }
        // Hash the password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        // Mark account as unverified
        customer.setVerified(false);
        Customer savedCustomer = customerRepository.save(customer);

        // Publish a Kafka event for new registration
        kafkaProducerService.sendMessage("New customer registered: " + savedCustomer.getEmail());

        // Send OTP to the registered email for confirmation
        otpService.createAndSendOtp(savedCustomer.getEmail(), true);

        return ResponseEntity.ok("Registration initiated. Please confirm your account using the OTP sent to your email.");
    }

    // 2. Confirm registration by verifying the OTP; marks account as verified
    @PostMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestParam String email,
                                                 @RequestParam String otp) {
        boolean validOtp = otpService.verifyOtp(email, otp);
        if (!validOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
        }
        Customer customer = customerOpt.get();
        customer.setVerified(true);
        customerRepository.save(customer);

        // Publish a Kafka event for registration confirmation
        kafkaProducerService.sendMessage("Customer verified: " + email);

        return ResponseEntity.ok("Registration confirmed successfully.");
    }

    // 3. Request a password reset (send OTP to registered email)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        otpService.createAndSendOtp(email, true);
        kafkaProducerService.sendMessage("Password reset requested for: " + email);
        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    // 4. Reset password using OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword) {
        boolean validOtp = otpService.verifyOtp(email, otp);
        if (!validOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
        }
        Customer customer = customerOpt.get();
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
        kafkaProducerService.sendMessage("Password reset successful for: " + email);
        return ResponseEntity.ok("Password reset successful.");
    }
}

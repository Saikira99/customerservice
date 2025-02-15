//package com.example.customerservice.controller;
//
//import com.example.customerservice.model.Customer;
//import com.example.customerservice.repository.CustomerRepository;
//import com.example.customerservice.service.KafkaProducerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/customers")
//public class CustomerController01 {
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private KafkaProducerService kafkaProducerService;
//
//    // Endpoint for customer registration
//    @PostMapping("/register")
//    public Customer register(@RequestBody Customer customer) {
//        // In a real-world application, add validation and password encryption.
//        Customer savedCustomer = customerRepository.save(customer);
//
//        // Publish a Kafka event indicating a new customer registration.
//        kafkaProducerService.sendMessage("New customer registered: " + savedCustomer.getName());
//
//        return savedCustomer;
//    }
//}

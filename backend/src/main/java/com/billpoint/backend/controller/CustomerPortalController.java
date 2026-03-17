package com.billpoint.backend.controller;

import com.billpoint.backend.model.Customer;
import com.billpoint.backend.repository.BillRepository;
import com.billpoint.backend.repository.CustomerRepository;
import com.billpoint.backend.repository.OfferRepository;
import com.billpoint.backend.security.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPortalController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/bills")
    public ResponseEntity<?> getMyBills(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<Customer> customers = customerRepository.findByUser_Id(userId);

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<Customer> customers = customerRepository.findByUser_Id(userId);

        return ResponseEntity.ok(customers);
    }
}
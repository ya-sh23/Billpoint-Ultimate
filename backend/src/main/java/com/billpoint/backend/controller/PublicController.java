package com.billpoint.backend.controller;

import com.billpoint.backend.dto.MessageResponse;
import com.billpoint.backend.model.ShopRequest;
import com.billpoint.backend.repository.ShopRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ShopRequestRepository shopRequestRepository;

    @PostMapping("/shop-request")
    public ResponseEntity<?> submitShopRequest(@RequestBody ShopRequest request) {
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        shopRequestRepository.save(request);
        
        return ResponseEntity.ok(new MessageResponse("Shop registration request submitted successfully. Admin will review it shortly."));
    }
}

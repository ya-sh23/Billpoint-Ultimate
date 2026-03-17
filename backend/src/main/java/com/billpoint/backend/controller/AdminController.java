// package com.billpoint.backend.controller;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.billpoint.backend.dto.ApprovalRequest;
// import com.billpoint.backend.dto.MessageResponse;
// import com.billpoint.backend.model.Shop;
// import com.billpoint.backend.model.ShopRequest;
// import com.billpoint.backend.model.Subscription;
// import com.billpoint.backend.model.User;
// import com.billpoint.backend.repository.ShopRepository;
// import com.billpoint.backend.repository.ShopRequestRepository;
// import com.billpoint.backend.repository.SubscriptionRepository;
// import com.billpoint.backend.repository.UserRepository;

// @CrossOrigin(origins = "*", maxAge = 3600)
// @RestController
// @RequestMapping("/api/admin")
// @PreAuthorize("hasRole('ADMIN')")
// public class AdminController {

//     @Autowired
//     private ShopRequestRepository shopRequestRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private ShopRepository shopRepository;

//     @Autowired
//     private SubscriptionRepository subscriptionRepository;

//     @Autowired
//     private PasswordEncoder encoder;

//     @GetMapping("/requests")
//     public ResponseEntity<?> getAllRequests() {
//         return ResponseEntity.ok(shopRequestRepository.findAll());
//     }

//     @PostMapping("/requests/{id}/approve")
//     public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestBody ApprovalRequest approvalRequest) {

//         Optional<ShopRequest> requestOpt = shopRequestRepository.findById(id);

//         if (requestOpt.isEmpty()) {
//             return ResponseEntity.badRequest().body(new MessageResponse("Error: Request not found."));
//         }

//         ShopRequest request = requestOpt.get();

//         if ("APPROVED".equals(request.getStatus())) {
//             return ResponseEntity.badRequest().body(new MessageResponse("Error: Request already approved."));
//         }

//         // 1️⃣ Create Shop Owner User

//         String defaultPassword = "password123";
//         String username = request.getEmail().split("@")[0] + "_" + System.currentTimeMillis() % 1000;

//         User owner = new User();
//         owner.setUsername(username);
//         owner.setEmail(request.getEmail());
//         owner.setPhone(request.getPhone());
//         owner.setRole("SHOP_OWNER");
//         owner.setPassword(encoder.encode(defaultPassword));
//         owner.setIsActive(true);
//         owner.setCreatedAt(LocalDateTime.now());

//         User savedOwner = userRepository.save(owner);

//         // 2️⃣ Create Shop

//         Shop shop = new Shop();
//         shop.setOwner(savedOwner);   // 🔥 changed
//         shop.setName(request.getShopName());
//         shop.setAddress(request.getAddress());
//         shop.setEmail(request.getEmail());
//         shop.setPhone(request.getPhone());
//         shop.setIsActive(true);
//         shop.setCreatedAt(LocalDateTime.now());

//         Shop savedShop = shopRepository.save(shop);

//         // 3️⃣ Create Subscription

//         Subscription sub = new Subscription();
//         sub.setShop(savedShop);  // 🔥 changed

//         sub.setPlanName(
//                 approvalRequest.getPlanName() != null ?
//                         approvalRequest.getPlanName() : "Basic Plan"
//         );

//         sub.setValidFrom(LocalDate.now());

//         int months = approvalRequest.getValidMonths() > 0 ?
//                 approvalRequest.getValidMonths() : 1;

//         sub.setValidTo(LocalDate.now().plusMonths(months));

//         sub.setIsActive(true);

//         subscriptionRepository.save(sub);

//         // 4️⃣ Update request status

//         request.setStatus("APPROVED");
//         request.setUpdatedAt(LocalDateTime.now());

//         shopRequestRepository.save(request);

//         return ResponseEntity.ok(
//                 new MessageResponse("Shop Request Approved. Shop Owner credentials generated.")
//         );
//     }

//     @PostMapping("/requests/{id}/reject")
//     public ResponseEntity<?> rejectRequest(@PathVariable Long id) {

//         Optional<ShopRequest> requestOpt = shopRequestRepository.findById(id);

//         if (requestOpt.isEmpty()) {
//             return ResponseEntity.badRequest().body(new MessageResponse("Error: Request not found."));
//         }

//         ShopRequest request = requestOpt.get();

//         request.setStatus("REJECTED");
//         request.setUpdatedAt(LocalDateTime.now());

//         shopRequestRepository.save(request);

//         return ResponseEntity.ok(new MessageResponse("Shop Request Rejected."));
//     }

//     @GetMapping("/shops")
//     public ResponseEntity<?> getAllShops() {
//         return ResponseEntity.ok(shopRepository.findAll());
//     }
// }

package com.billpoint.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.billpoint.backend.dto.ApprovalRequest;
import com.billpoint.backend.dto.MessageResponse;
import com.billpoint.backend.model.Shop;
import com.billpoint.backend.model.ShopRequest;
import com.billpoint.backend.model.Subscription;
import com.billpoint.backend.model.User;
import com.billpoint.backend.repository.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ShopRequestRepository shopRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ GET ALL REQUESTS
    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(shopRequestRepository.findAll());
    }

    // ✅ APPROVE REQUEST
    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id,
                                            @RequestBody ApprovalRequest approvalRequest) {

        Optional<ShopRequest> requestOpt = shopRequestRepository.findById(id);

        if (requestOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Request not found."));
        }

        ShopRequest request = requestOpt.get();

        if ("APPROVED".equals(request.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Already approved."));
        }

        // 🔥 ACTIVATE EXISTING USER IF EXISTS
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        User owner;

        if (existingUser.isPresent()) {
            owner = existingUser.get();
            owner.setIsActive(true); // ✅ APPROVED
        } else {
            // CREATE NEW USER (fallback)
            String defaultPassword = "password123";
            String username = request.getEmail().split("@")[0] + "_" + System.currentTimeMillis() % 1000;

            owner = new User();
            owner.setUsername(username);
            owner.setEmail(request.getEmail());
            owner.setPhone(request.getPhone());
            owner.setRole("SHOP_OWNER");
            owner.setPassword(encoder.encode(defaultPassword));
            owner.setIsActive(true); // ✅ ACTIVE AFTER APPROVAL
            owner.setCreatedAt(LocalDateTime.now());
        }

        User savedOwner = userRepository.save(owner);

        // ✅ CREATE SHOP
        Shop shop = new Shop();
        shop.setOwner(savedOwner);
        shop.setName(request.getShopName());
        shop.setAddress(request.getAddress());
        shop.setEmail(request.getEmail());
        shop.setPhone(request.getPhone());
        shop.setPanCard(request.getPanCard()); // Transfer PAN
        shop.setGstin(request.getGstin());   // Transfer GSTIN
        shop.setIsActive(true);
        shop.setCreatedAt(LocalDateTime.now());

        Shop savedShop = shopRepository.save(shop);

        // ✅ CREATE SUBSCRIPTION
        Subscription sub = new Subscription();
        sub.setShop(savedShop);

        sub.setPlanName(
                approvalRequest.getPlanName() != null
                        ? approvalRequest.getPlanName()
                        : "Basic Plan"
        );

        sub.setValidFrom(LocalDate.now());

        int months = approvalRequest.getValidMonths() > 0
                ? approvalRequest.getValidMonths()
                : 1;

        sub.setValidTo(LocalDate.now().plusMonths(months));
        sub.setIsActive(true);

        subscriptionRepository.save(sub);

        // ✅ UPDATE REQUEST
        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());
        shopRequestRepository.save(request);

        return ResponseEntity.ok(
                new MessageResponse("Shop approved and account activated.")
        );
    }

    // ✅ REJECT
    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {

        Optional<ShopRequest> requestOpt = shopRequestRepository.findById(id);

        if (requestOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Request not found."));
        }

        ShopRequest request = requestOpt.get();
        request.setStatus("REJECTED");
        request.setUpdatedAt(LocalDateTime.now());

        shopRequestRepository.save(request);

        return ResponseEntity.ok(
                new MessageResponse("Shop Request Rejected.")
        );
    }

    // ✅ GET SHOPS
    @GetMapping("/shops")
    public ResponseEntity<?> getAllShops() {
        return ResponseEntity.ok(shopRepository.findAll());
    }
}
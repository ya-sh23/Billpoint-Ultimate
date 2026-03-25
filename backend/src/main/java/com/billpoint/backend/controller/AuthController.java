// package com.billpoint.backend.controller;

// import com.billpoint.backend.dto.JwtResponse;
// import com.billpoint.backend.dto.LoginRequest;
// import com.billpoint.backend.dto.MessageResponse;
// import com.billpoint.backend.dto.SignupRequest;
// import com.billpoint.backend.model.User;
// import com.billpoint.backend.repository.UserRepository;
// import com.billpoint.backend.security.JwtUtils;
// import com.billpoint.backend.security.UserDetailsImpl;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;

// @CrossOrigin(origins = "*", maxAge = 3600)
// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {
//     @Autowired
//     AuthenticationManager authenticationManager;

//     @Autowired
//     UserRepository userRepository;

//     @Autowired
//     PasswordEncoder encoder;

//     @Autowired
//     JwtUtils jwtUtils;

//     @PostMapping("/signin")
//     public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

//         Authentication authentication = authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

//         SecurityContextHolder.getContext().setAuthentication(authentication);
//         String jwt = jwtUtils.generateJwtToken(authentication);

//         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//         String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

//         return ResponseEntity.ok(new JwtResponse(jwt,
//                 userDetails.getId(),
//                 userDetails.getUsername(),
//                 userDetails.getEmail(),
//                 role));
//     }

//     @PostMapping("/signup")
//     public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//         if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//             return ResponseEntity
//                     .badRequest()
//                     .body(new MessageResponse("Error: Username is already taken!"));
//         }

//         if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//             return ResponseEntity
//                     .badRequest()
//                     .body(new MessageResponse("Error: Email is already in use!"));
//         }

//         // Create new user's account
//         User user = new User();
//         user.setUsername(signUpRequest.getUsername());
//         user.setEmail(signUpRequest.getEmail());
//         user.setPassword(encoder.encode(signUpRequest.getPassword()));
//         user.setPhone(signUpRequest.getPhone());
//         user.setCreatedAt(LocalDateTime.now());
//         user.setIsActive(true);

//         String strRole = signUpRequest.getRole();
//         if (strRole == null) {
//             user.setRole("CUSTOMER"); // Default role
//         } else {
//             user.setRole(strRole.toUpperCase());
//         }

//         userRepository.save(user);

//         return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//     }
// }

package com.billpoint.backend.controller;

import com.billpoint.backend.dto.JwtResponse;
import com.billpoint.backend.dto.LoginRequest;
import com.billpoint.backend.dto.MessageResponse;
import com.billpoint.backend.dto.SignupRequest;
import com.billpoint.backend.model.User;
import com.billpoint.backend.model.ShopRequest;
import com.billpoint.backend.repository.ShopRequestRepository;
import com.billpoint.backend.repository.UserRepository;
import com.billpoint.backend.security.JwtUtils;
import com.billpoint.backend.security.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShopRequestRepository shopRequestRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // ✅ LOGIN
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        // 🔥 BLOCK LOGIN IF NOT APPROVED
        if (!user.getIsActive()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Account not approved yet. Please wait for admin approval."));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String role = userDetails.getAuthorities().iterator()
                .next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role
        ));
    }

    // ✅ REGISTER
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setCreatedAt(LocalDateTime.now());

        String role = signUpRequest.getRole() != null
                ? signUpRequest.getRole().toUpperCase()
                : "CUSTOMER";

        user.setRole(role);

        // 🔥 MAIN LOGIC
        if (role.equals("SHOP_OWNER")) {
            user.setIsActive(false); // requires admin approval
            
            // Create ShopRequest for Admin to approve
            ShopRequest shopRequest = new ShopRequest();
            shopRequest.setOwnerName(user.getUsername());
            shopRequest.setShopName(signUpRequest.getBusinessName());
            shopRequest.setEmail(user.getEmail());
            shopRequest.setPhone(user.getPhone());
            shopRequest.setAddress(signUpRequest.getAddress());
            shopRequest.setPanCard(signUpRequest.getPanCard());
            shopRequest.setGstin(signUpRequest.getGstin());
            shopRequest.setStatus("PENDING");
            shopRequest.setCreatedAt(LocalDateTime.now());
            
            // We need ShopRequestRepository here
            shopRequestRepository.save(shopRequest);

        } else {
            user.setIsActive(true);
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                new MessageResponse("User registered successfully! Please wait for admin approval.")
        );
    }
}
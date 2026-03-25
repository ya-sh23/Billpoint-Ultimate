package com.billpoint.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 8, max = 40)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*#])[A-Za-z\\d!@#$%^&*#]{8,}$", 
             message = "Password must be at least 8 characters long, contain one uppercase letter, one number and one special character")
    private String password;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    
    private String role; 
    
    // Shop Owner specific fields
    private String businessName;

    @Pattern(regexp = "^([A-Z]{5}[0-9]{4}[A-Z]{1})?$", message = "Invalid PAN Card format")
    private String panCard;

    @Pattern(regexp = "^([0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1})?$", message = "Invalid GSTIN format")
    private String gstin;
    
    private String address;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getPanCard() { return panCard; }
    public void setPanCard(String panCard) { this.panCard = panCard; }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

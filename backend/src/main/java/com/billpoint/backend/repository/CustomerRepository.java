package com.billpoint.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billpoint.backend.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // find customers linked to a user
    List<Customer> findByUser_Id(Long userId);

    // find customer by phone and shop
    Optional<Customer> findByPhoneAndShop_Id(String phone, Long shopId);

}
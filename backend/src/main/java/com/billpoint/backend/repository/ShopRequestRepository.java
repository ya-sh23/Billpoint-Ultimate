package com.billpoint.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.ShopRequest;

public interface ShopRequestRepository extends JpaRepository<ShopRequest, Long> {

    List<ShopRequest> findByStatus(String status);

}
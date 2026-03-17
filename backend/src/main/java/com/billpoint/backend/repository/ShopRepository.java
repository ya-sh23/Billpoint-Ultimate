package com.billpoint.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByOwner_Id(Long ownerId);

}
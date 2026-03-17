package com.billpoint.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billpoint.backend.model.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByShop_IdAndIsActiveTrue(Long shopId);

}
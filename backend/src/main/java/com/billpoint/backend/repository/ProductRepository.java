package com.billpoint.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShop_Id(Long shopId);

}
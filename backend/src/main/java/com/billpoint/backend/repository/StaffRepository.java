package com.billpoint.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByShop_Id(Long shopId);

    Staff findByUser_Id(Long userId);

}
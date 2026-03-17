package com.billpoint.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.BillItem;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    List<BillItem> findByBill_Id(Long billId);

}
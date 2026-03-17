package com.billpoint.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billpoint.backend.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByShop_IdAndDate(Long shopId, LocalDate date);

    Attendance findByStaff_IdAndDate(Long staffId, LocalDate date);

    List<Attendance> findByStaff_IdOrderByDateDesc(Long staffId);

}
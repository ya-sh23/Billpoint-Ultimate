package com.billpoint.backend.controller;

import com.billpoint.backend.dto.MessageResponse;
import com.billpoint.backend.model.Attendance;
import com.billpoint.backend.model.Staff;
import com.billpoint.backend.repository.AttendanceRepository;
import com.billpoint.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('STAFF', 'SHOP_OWNER')")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Attendance attendance) {
        if (attendance.getStaff() == null || attendance.getStaff().getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Staff info missing"));
        }

        Staff staff = staffRepository.findById(attendance.getStaff().getId())
                .orElseThrow(() -> new RuntimeException("Error: Staff not found"));

        // Check for duplicate entry for the same date
        Attendance existing = attendanceRepository.findByStaff_IdAndDate(staff.getId(), attendance.getDate());
        if (existing != null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Attendance already marked for this date"));
        }

        attendance.setStaff(staff);
        attendance.setShop(staff.getShop());
        attendance.setStatus("PRESENT");
        
        attendanceRepository.save(attendance);

        return ResponseEntity.ok(new MessageResponse("Attendance marked successfully"));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<Attendance>> getStaffAttendance(@PathVariable Long staffId) {
        List<Attendance> history = attendanceRepository.findByStaff_IdOrderByDateDesc(staffId);
        return ResponseEntity.ok(history);
    }
}

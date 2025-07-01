package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Attendance;
import com.smartEdu.SmartEduBackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/save")
    public Attendance saveAttendance(@RequestBody Attendance attendance) {
        return attendanceService.saveAttendance(attendance);
    }

    @GetMapping("/class/today")
    public List<Attendance> getTodayAttendance(@RequestParam String classId) {
        return attendanceService.getTodayAttendanceByClass(classId);
    }

    @GetMapping("/class/range")
    public List<Attendance> getClassAttendanceInRange(
            @RequestParam String classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return attendanceService.getClassAttendanceBetween(classId, start, end);
    }

    @GetMapping("/student/range")
    public List<Attendance> getStudentAttendanceInRange(
            @RequestParam String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return attendanceService.getStudentAttendanceBetween(studentId, start, end);
    }

    @GetMapping("/class/days")
    public long countSchoolOpenDaysForClass(
            @RequestParam String classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (start != null && end != null) {
            return attendanceService.countSchoolDaysForClass(classId, start, end);
        } else {
            return attendanceService.countSchoolDaysForClass(classId);
        }
    }
}

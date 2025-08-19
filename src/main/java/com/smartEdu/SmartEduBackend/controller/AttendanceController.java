package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Attendance;
import com.smartEdu.SmartEduBackend.entity.AttendanceRequest;
import com.smartEdu.SmartEduBackend.service.AttendanceService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<ResponseUtil> saveAttendance(@RequestBody AttendanceRequest attendance) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Attendance saved successfully", attendanceService.saveAttendance(attendance)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/today/{classId}")
    public ResponseEntity<ResponseUtil> getTodayAttendance(@PathVariable String classId) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Today's class attendance fetched", attendanceService.getTodayAttendanceByClass(classId)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/year/{classId}")
    public ResponseEntity<ResponseUtil> getAllStudentsAllAttendanceByClassId(@PathVariable String classId) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Today's class attendance fetched", attendanceService.getAllStudentsAllAttendanceByClassId(classId)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/by-student-id/{studentId}")
    public ResponseEntity<ResponseUtil> getAllAttendanceByStudentId(@PathVariable String studentId) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Today's class attendance fetched",
                            attendanceService.getAllAttendanceByStudentId(studentId)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/range")
    public ResponseEntity<ResponseUtil> getClassAttendanceInRange(
            @RequestParam String classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            List<Attendance> result = attendanceService.getClassAttendanceBetween(classId, start, end);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Class attendance fetched", result));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student/range")
    public ResponseEntity<ResponseUtil> getStudentAttendanceInRange(
            @RequestParam String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            List<Attendance> result = attendanceService.getStudentAttendanceBetween(studentId, start, end);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Student attendance fetched", result));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/days")
    public ResponseEntity<ResponseUtil> countSchoolOpenDaysForClass(
            @RequestParam String classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            long days = (start != null && end != null)
                    ? attendanceService.countSchoolDaysForClass(classId, start, end)
                    : attendanceService.countSchoolDaysForClass(classId);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Open day count calculated", days));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}

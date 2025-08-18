package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Attendance;
import com.smartEdu.SmartEduBackend.entity.AttendanceRequest;
import com.smartEdu.SmartEduBackend.repo.AttendanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepo attendanceRepository;

    public AttendanceRequest saveAttendance(AttendanceRequest attendance) {
        for (int i = 0; i < attendance.getAttendance().size(); i++) {
            attendanceRepository.save(
                    Attendance.builder()
                            .date(attendance.getDate())
                            .classId(attendance.getClassId())
                            .studentId(attendance.getAttendance().get(i).getStudentId())
                            .status(attendance.getAttendance().get(i).getStatus())
                            .build()
            );
        }
        return attendance;
    }

    public List<Attendance> getTodayAttendanceByClass(String classId) {
        return attendanceRepository.findByClassIdAndDate(classId, LocalDate.now());
    }

    public List<Attendance> getClassAttendanceBetween(String classId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByClassIdAndDateBetween(classId, start, end);
    }

    public List<Attendance> getStudentAttendanceBetween(String studentId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, start, end);
    }

    public long countSchoolDaysForClass(String classId, LocalDate start, LocalDate end) {
        return attendanceRepository.countDistinctByClassIdAndDateBetween(classId, start, end);
    }

    public long countSchoolDaysForClass(String classId) {
        return attendanceRepository.countDistinctByClassId(classId);
    }
}

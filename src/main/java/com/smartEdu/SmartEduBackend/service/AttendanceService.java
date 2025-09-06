package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.AttendanceStatus;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private AttendanceRepo attendanceRepository;

    @Autowired
    private StudentRepo studentRepo;

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

    public List<AttendanceResponse> getTodayAttendanceByClass(String classId) {
        List<Attendance> byClassIdAndDate = attendanceRepository.findByClassIdAndDate(classId, LocalDate.now());
        List<AttendanceResponse> attendanceResponses = new ArrayList<>();
        for (Attendance a : byClassIdAndDate) {
            attendanceResponses.add(AttendanceResponse.builder()
                    .studentId(a.getStudentId())
                    .studentName(studentRepo.findById(a.getStudentId()).get().getFullNameWithInitials())
                    .status(a.getStatus())
                    .build());
        }

        return attendanceResponses;
    }

    public List<Attendance> getAllAttendanceByStudentId(String studentId) {
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.parse(year + "-01-01");
        LocalDate today = LocalDate.now().plusDays(1);
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, today);
    }


    public AttendanceResponse getAllAttendanceByStudentIdToPrincipal(String studentId) {
        Student student = studentRepo.findById(studentId).get();
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.parse(year + "-01-01");
        LocalDate today = LocalDate.now().plusDays(1);

        List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, today);
        int totalDays = attendanceList.size();
        int totalAbsent = 0;
        int totalAttended = 0;

        for (Attendance a : attendanceList) {
            if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                totalAbsent++;
            } else {
                totalAttended++;
            }
        }

        return AttendanceResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullNameWithInitials())
                .totalDays(totalDays)
                .totalAttended(totalAttended)
                .totalAbsent(totalAbsent)
                .attendedRate(((double) totalAttended / totalDays) * 100)
                .build();

    }

    public AttendanceResponse getAllAttendanceByStudentIdToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.parse(year + "-01-01");
        LocalDate today = LocalDate.now().plusDays(1);

        List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndDateBetween(student.getId(), startDate, today);
        int totalDays = attendanceList.size();
        int totalAbsent = 0;
        int totalAttended = 0;

        for (Attendance a : attendanceList) {
            if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                totalAbsent++;
            } else {
                totalAttended++;
            }
        }

        return AttendanceResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullNameWithInitials())
                .totalDays(totalDays)
                .totalAttended(totalAttended)
                .totalAbsent(totalAbsent)
                .attendedRate(((double) totalAttended / totalDays) * 100)
                .build();

    }


    public Attendance getTodayAttendanceStatus(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        LocalDate today = LocalDate.now();
        Optional byStudentIdAndDate = attendanceRepository.findByStudentIdAndDate(student.getId(), today);
        if (byStudentIdAndDate.isEmpty()) {
            return null;
        }
        Attendance attendance= (Attendance) byStudentIdAndDate.get();
        ClassRoom classRoom = classRoomRepo.findById(attendance.getClassId()).get();
        Teacher teacher = teacherRepo.findById(classRoom.getClassTeacherId()).get();
        attendance.setClassId(userRepo.findByProfileId(teacher.getId()).getContact());
        return (Attendance) byStudentIdAndDate.get();
    }

    public List<AttendanceResponse> getAllStudentsAllAttendanceByClassId(String classId) {
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.parse(year + "-01-01");
        LocalDate today = LocalDate.now().plusDays(1);
        List<AttendanceResponse> attendanceResponses = new ArrayList<>();

        List<Attendance> byClassIdAndDate = attendanceRepository.findByClassIdAndDateBetween(classId, startDate, today);
        int totalDays = byClassIdAndDate.size();

        List<Student> byClassId = studentRepo.findByClassId(classId);
        for (Student s : byClassId) {
            List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndDateBetween(s.getId(), startDate, today);
            int totalAbsent = 0;
            int totalAttended = 0;

            for (Attendance a : attendanceList) {
                if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                    totalAbsent++;
                } else {
                    totalAttended++;
                }
            }

            attendanceResponses.add(AttendanceResponse.builder()
                    .studentId(s.getId())
                    .studentName(s.getFullNameWithInitials())
                    .totalDays((totalAbsent+totalAttended))
                    .totalAttended(totalAttended)
                    .totalAbsent(totalAbsent)
                    .attendedRate(((double) totalAttended / (totalAbsent+totalAttended)) * 100)
                    .build());
        }

        return attendanceResponses;
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

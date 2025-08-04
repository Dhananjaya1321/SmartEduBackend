package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.StudentResponse;
import com.smartEdu.SmartEduBackend.repo.ClassRoomRepo;
import com.smartEdu.SmartEduBackend.repo.GradesRepo;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    @Autowired
    private SchoolRepo schoolRepo;


    public Student save(Student student, String token) {
        Optional<Student> existingStudent = studentRepo.findByRegistrationNumber(student.getRegistrationNumber());
        if (existingStudent.isPresent())
            throw new RuntimeException("Registration number already exists!");

        String schoolId = jwtUtil.extractInstitutionId(token);
        student.setSchoolId(schoolId);
        Student save = studentRepo.save(student);

        ClassRoom classRoom = classRoomRepo.findById(student.getClassId()).get();
        classRoom.getStudentIds().add(save.getId());
        classRoomRepo.save(classRoom);

        return save;
    }

    public Student update(String id, Student student) {
        studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Student not found!"));

        student.setId(id);
        return studentRepo.save(student);
    }

    public void delete(String id) {
        studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Student not found!"));

        studentRepo.deleteById(id);
    }

    public Optional<Student> findById(String id) {
        return studentRepo.findById(id);
    }

    public List<StudentResponse> findAll(int page, int size, String token) {
        String schoolId = jwtUtil.extractInstitutionId(token);
        List<Student> allBySchoolId = studentRepo.findAllBySchoolId(schoolId);
        List<StudentResponse> studentResponses = new ArrayList<>();

        for (Student s:allBySchoolId){
            String gradeName = gradesRepo.findById(s.getGradeId()).get().getGradeName();
            String className = classRoomRepo.findById(s.getClassId()).get().getClassName();
            StudentResponse studentResponse= StudentResponse.builder()
                    .id(s.getId())
                    .entryDate(s.getEntryDate())
                    .fatherName(s.getFatherName())
                    .fullNameWithInitials(s.getFullNameWithInitials())
                    .dateOfBirth(s.getDateOfBirth())
                    .motherName(s.getMotherName())
                    .motherContact(s.getMotherContact())
                    .fullName(s.getFullName())
                    .fatherContact(s.getFatherContact())
                    .address(s.getAddress())
                    .registrationNumber(s.getRegistrationNumber())
                    .gradeId(s.getGradeId())
                    .gradeName(gradeName)
                    .classId(s.getClassId())
                    .className(className)
                    .schoolId(s.getSchoolId())
                    .achievements(s.getAchievements())
                    .build();

            studentResponses.add(studentResponse);
        }

        return studentResponses;
    }


    public String generateRegistrationNumber(String token) {
        String schoolId = jwtUtil.extractInstitutionId(token);
        School school = schoolRepo.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));

        String schoolNumber = school.getSchoolNumber(); // used in final reg number
        int year = Year.now().getValue();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        long studentCountThisYear = studentRepo.countBySchoolIdAndEntryDateBetween(schoolId, start, end);
        String studentNumber = String.format("%04d", studentCountThisYear + 1);

        return schoolNumber + "-" + year + "-" + studentNumber;
    }

}

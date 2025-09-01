package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
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
    private UserRepo userRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

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


    public List<StudentResponse> findClassAllStudentsByClassId(String id) {
        ClassRoom classRoom = classRoomRepo.findById(id).get();
        List<StudentResponse> studentList = new ArrayList<>();
        for (String s : classRoom.getStudentIds()) {
            Student student = studentRepo.findById(s).get();

            StudentResponse studentResponse = StudentResponse
                    .builder()
                    .id(student.getId())
                    .fullNameWithInitials(student.getFullNameWithInitials())
                    .registrationNumber(student.getRegistrationNumber())
                    .build();
            studentList.add(studentResponse);
        }
        return studentList;
    }

    public List<StudentResponse> findMyClassAllStudents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        ClassRoom byClassTeacherId = classRoomRepo.findByClassTeacherId(profileId);

        List<StudentResponse> studentList = new ArrayList<>();
        for (String s : byClassTeacherId.getStudentIds()) {
            Student student = studentRepo.findById(s).get();

            StudentResponse studentResponse = StudentResponse
                    .builder()
                    .id(student.getId())
                    .fullNameWithInitials(student.getFullNameWithInitials())
                    .registrationNumber(student.getRegistrationNumber())
                    .gradeName(gradesRepo.findById(student.getGradeId()).get().getGradeName())
                    .gradeId(student.getGradeId())
                    .className(classRoomRepo.findById(student.getClassId()).get().getClassName())
                    .classId(student.getClassId())
                    .build();
            studentList.add(studentResponse);
        }
        return studentList;
    }

    public StudentResponse getStudentByStudentId(String id, String token) {
        Student student = studentRepo.findById(id).get();

        String gradeName = gradesRepo.findById(student.getGradeId()).get().getGradeName();
        String className = classRoomRepo.findById(student.getClassId()).get().getClassName();

        return StudentResponse.builder()
                .id(student.getId())
                .entryDate(student.getEntryDate())
                .fatherName(student.getFatherName())
                .fullNameWithInitials(student.getFullNameWithInitials())
                .dateOfBirth(student.getDateOfBirth())
                .motherName(student.getMotherName())
                .motherContact(student.getMotherContact())
                .fullName(student.getFullName())
                .fatherContact(student.getFatherContact())
                .address(student.getAddress())
                .registrationNumber(student.getRegistrationNumber())
                .gradeId(student.getGradeId())
                .gradeName(gradeName)
                .classId(student.getClassId())
                .className(className)
                .schoolId(student.getSchoolId())
                .achievements(student.getAchievements())
                .build();
    }


    public StudentResponse findStudentToParent(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();


        String gradeName = gradesRepo.findById(student.getGradeId()).get().getGradeName();
        String className = classRoomRepo.findById(student.getClassId()).get().getClassName();

        return StudentResponse.builder()
                .id(student.getId())
                .entryDate(student.getEntryDate())
                .fatherName(student.getFatherName())
                .fullNameWithInitials(student.getFullNameWithInitials())
                .dateOfBirth(student.getDateOfBirth())
                .motherName(student.getMotherName())
                .motherContact(student.getMotherContact())
                .fullName(student.getFullName())
                .fatherContact(student.getFatherContact())
                .address(student.getAddress())
                .registrationNumber(student.getRegistrationNumber())
                .gradeId(student.getGradeId())
                .gradeName(gradeName)
                .classId(student.getClassId())
                .className(className)
                .schoolId(student.getSchoolId())
                .achievements(student.getAchievements())
                .build();
    }

    public List<StudentResponse> findAll(int page, int size, String token) {
        String schoolId = jwtUtil.extractInstitutionId(token);
        List<Student> allBySchoolId = studentRepo.findAllBySchoolId(schoolId);
        List<StudentResponse> studentResponses = new ArrayList<>();

        for (Student s : allBySchoolId) {
            String gradeName = gradesRepo.findById(s.getGradeId()).get().getGradeName();
            String className = classRoomRepo.findById(s.getClassId()).get().getClassName();
            StudentResponse studentResponse = StudentResponse.builder()
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


    public List<Student> searchStudentsByName(String token, String inputValue, String selectedApplication) {
        String schoolId = jwtUtil.extractInstitutionId(token);
        int grade = switch (selectedApplication) {
            case "ol" -> 11;
            case "al" -> 13;
            case "g5" -> 5;
            case "nic" -> 10;
            default -> -1;
        };
        if (grade == -1) return List.of();
        Grades byGradeName;
        List<Student> students;
        if (grade == 13) {
            students = new ArrayList<>();
            byGradeName = gradesRepo.findByGradeName("13(Science)");
            List<Student> scienceStudents = studentRepo.findAllBySchoolIdAndFullNameAndGradeId(schoolId, inputValue, byGradeName.getId());
            if (!scienceStudents.isEmpty()) {
                students.addAll(scienceStudents);
            }
            byGradeName = gradesRepo.findByGradeName("13(Commerce)");
            List<Student> commerceStudents = studentRepo.findAllBySchoolIdAndFullNameAndGradeId(schoolId, inputValue, byGradeName.getId());
            if (!commerceStudents.isEmpty()) {
                students.addAll(commerceStudents);
            }
            byGradeName = gradesRepo.findByGradeName("13(Tech)");
            List<Student> techStudents = studentRepo.findAllBySchoolIdAndFullNameAndGradeId(schoolId, inputValue, byGradeName.getId());
            if (!techStudents.isEmpty()) {
                students.addAll(techStudents);
            }
            byGradeName = gradesRepo.findByGradeName("13(Arts)");
            List<Student> artsStudents = studentRepo.findAllBySchoolIdAndFullNameAndGradeId(schoolId, inputValue, byGradeName.getId());
            if (!artsStudents.isEmpty()) {
                students.addAll(artsStudents);
            }
        } else {
            byGradeName = gradesRepo.findByGradeName(String.valueOf(grade));
            students = studentRepo.findAllBySchoolIdAndFullNameAndGradeId(schoolId, inputValue, byGradeName.getId());
        }
        return students;
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

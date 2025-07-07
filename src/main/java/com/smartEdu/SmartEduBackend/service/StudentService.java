package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Service
@Transactional
public class StudentService {
    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private SchoolRepo schoolRepo;


    public Student save(Student student) {
        // Check if registration number already exists
        Optional<Student> existingStudent = studentRepo.findByRegistrationNumber(student.getRegistrationNumber());
        if (existingStudent.isPresent())
            throw new RuntimeException("Registration number already exists!");

        return studentRepo.save(student);
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

    public Page<Student> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepo.findAll(pageable);
    }


    public String generateRegistrationNumber(String schoolId) {
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

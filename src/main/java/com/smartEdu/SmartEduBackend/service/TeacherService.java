package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Teacher;
import com.smartEdu.SmartEduBackend.entity.TeacherRegisterRequest;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.TeacherRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Create Teacher and User together
    public Teacher registerTeacherWithUser(TeacherRegisterRequest request) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        Teacher teacher = Teacher.builder()
                .fullName(request.getFullName())
                .schoolId(request.getSchoolId())
                .build();

        Teacher savedTeacher = teacherRepo.save(teacher);

        User user = User.builder()
                .nic(request.getNic())
                .contact(request.getContact())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .email(request.getEmail())
                .role(Role.TEACHER)
                .active(true)
                .profileId(savedTeacher.getId())
                .institutionID(request.getSchoolId())
                .build();

        userRepo.save(user);

        return savedTeacher;
    }

    public Teacher update(String id, Teacher updated) {
        teacherRepo.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found!"));
        updated.setId(id);
        return teacherRepo.save(updated);
    }

    public void delete(String id) {
        teacherRepo.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found!"));
        teacherRepo.deleteById(id);
    }

    public Optional<Teacher> findById(String id) {
        return teacherRepo.findById(id);
    }

    public Page<Teacher> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return teacherRepo.findAll(pageable);
    }
}

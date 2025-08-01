package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.TeacherRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

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
                .name(request.getFullName())
                .active(true)
                .profileId(savedTeacher.getId())
                .institutionID(request.getSchoolId())
                .build();

        userRepo.save(user);

        return savedTeacher;
    }

    public TeacherRegisterRequest update(String id, TeacherRegisterRequest updated) {
        Teacher oldTeacher = teacherRepo.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found!"));
        User oldUser = userRepo.findByProfileId(id);

        Teacher teacher = Teacher.builder()
                .id(id)
                .fullName(updated.getFullName())
                .schoolId(oldTeacher.getSchoolId())
                .build();

        teacherRepo.save(teacher);

        User user = User.builder()
                .id(oldUser.getId())
                .nic(updated.getNic())
                .contact(updated.getContact())
                .username(updated.getUsername())
                .password(oldUser.getPassword())
                .address(updated.getAddress())
                .email(updated.getEmail())
                .role(Role.TEACHER)
                .name(updated.getFullName())
                .active(true)
                .profileId(oldUser.getProfileId())
                .institutionID(oldUser.getInstitutionID())
                .build();

        userRepo.save(user);

        return updated;
    }

    public void delete(String id) {
        teacherRepo.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found!"));
        teacherRepo.deleteById(id);
    }

    public Optional<Teacher> findById(String id) {
        return teacherRepo.findById(id);
    }

    public Page<TeacherResponse> findAllForZonalOffice(int page, int size, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).orElse(null);

        if (zonalEducationOffice == null)
            return Page.empty(); // or throw an exception


        List<TeacherResponse> allTeachers = new ArrayList<>();
        for (String schoolId : zonalEducationOffice.getSchoolsIds()) {
            List<Teacher> teachers = teacherRepo.findAllBySchoolId(schoolId);
            School school = schoolRepo.findById(schoolId).orElse(null);

            if (school == null) return Page.empty(); // or throw an exception

            for (Teacher teacher : teachers) {
                User user = userRepo.findByProfileId(teacher.getId());

                TeacherResponse teacherResponse = TeacherResponse.builder()
                        .id(teacher.getId())
                        .schoolId(teacher.getSchoolId())
                        .schoolName(school.getSchoolName())
                        .fullName(teacher.getFullName())
                        .nic(user.getNic())
                        .contact(user.getContact())
                        .username(user.getUsername())
                        .address(user.getAddress())
                        .email(user.getEmail())
                        .build();

                allTeachers.add(teacherResponse);
            }
        }

        // Manual Pagination Logic
        int start = page * size;
        int end = Math.min(start + size, allTeachers.size());

        if (start > end) {
            return Page.empty(); // no content for this page
        }

        List<TeacherResponse> pagedList = allTeachers.subList(start, end);
        return new PageImpl<>(pagedList, PageRequest.of(page, size), allTeachers.size());
    }

    public Page<TeacherResponse> findAllForSchool(int page, int size, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        School school = schoolRepo.findById(institutionId).orElse(null);

        if (school == null)
            return Page.empty(); // or throw an exception


        List<TeacherResponse> allTeachers = new ArrayList<>();
        List<Teacher> teachers = teacherRepo.findAllBySchoolId(institutionId);

        for (Teacher teacher : teachers) {
            User user = userRepo.findByProfileId(teacher.getId());
            TeacherResponse teacherResponse = TeacherResponse.builder()
                    .id(teacher.getId())
                    .schoolId(teacher.getSchoolId())
                    .schoolName(school.getSchoolName())
                    .fullName(teacher.getFullName())
                    .nic(user.getNic())
                    .contact(user.getContact())
                    .username(user.getUsername())
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .build();

            allTeachers.add(teacherResponse);
        }

        // Manual Pagination Logic
        int start = page * size;
        int end = Math.min(start + size, allTeachers.size());

        if (start > end) {
            return Page.empty(); // no content for this page
        }

        List<TeacherResponse> pagedList = allTeachers.subList(start, end);
        return new PageImpl<>(pagedList, PageRequest.of(page, size), allTeachers.size());
    }
}

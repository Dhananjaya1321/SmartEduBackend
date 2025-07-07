package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.ParentRegisterRequest;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Parent registerParent(ParentRegisterRequest request) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Step 1: Create User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nic(request.getNic())
                .contact(request.getContact())
                .address(request.getAddress())
                .email(request.getEmail())
                .role(Role.PARENT)
                .institutionID("PARENT") // paren dont want this
                .active(true)
                .build();

        user = userRepo.save(user);

        // Step 2: Create Parent profile
        Parent parent = Parent.builder()
                .fullName(request.getFullName())
                .studentIds(List.of())
                .build();

        parent = parentRepo.save(parent);

        // Step 3: Update user with parent profileId
        user.setProfileId(parent.getId());
        userRepo.save(user);

        return parent;
    }

    public Parent update(String id, Parent updatedParent) {
        parentRepo.findById(id).orElseThrow(() -> new RuntimeException("Parent not found!"));
        updatedParent.setId(id);
        return parentRepo.save(updatedParent);
    }

    public Student verifyAndLinkStudent(String registrationNumber, String parentName, String contact, String parentId) {
        Optional<Student> studentOpt = studentRepo.findByRegistrationNumber(registrationNumber);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Invalid registration number.");
        }

        Student student = studentOpt.get();

        boolean isMotherMatch = student.getMotherName().equalsIgnoreCase(parentName)
                && student.getMotherContact().equals(contact);
        boolean isFatherMatch = student.getFatherName().equalsIgnoreCase(parentName)
                && student.getFatherContact().equals(contact);

        if (!isMotherMatch && !isFatherMatch) {
            throw new RuntimeException("Parent details do not match.");
        }

        Parent parent = parentRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found."));

        if (!parent.getStudentIds().contains(student.getId())) {
            parent.getStudentIds().add(student.getId());
            parentRepo.save(parent);
        }

        return student;
    }

    public void delete(String id) {
        parentRepo.findById(id).orElseThrow(() -> new RuntimeException("Parent not found!"));
        parentRepo.deleteById(id);
    }

    public Optional<Parent> findById(String id) {
        parentRepo.findById(id).orElseThrow(() -> new RuntimeException("Parent not found!"));
        return parentRepo.findById(id);
    }

    public List<Parent> findAll(int page, int size) {
        return parentRepo.findAll(PageRequest.of(page, size)).getContent();
    }
}

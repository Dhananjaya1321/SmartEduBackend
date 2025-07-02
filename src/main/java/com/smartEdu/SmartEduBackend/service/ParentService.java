package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.ParentRegisterRequest;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
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
                .active(true)
                .build();

        user = userRepo.save(user);

        // Step 2: Create Parent profile
        Parent parent = Parent.builder()
                .fullName(request.getFullName())
                .nic(request.getNic())
                .address(request.getAddress())
                .contact(request.getContact())
                .email(request.getEmail())
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

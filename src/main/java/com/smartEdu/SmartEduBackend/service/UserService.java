package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.CustomUserDetails;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User save(User user) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Hash the password before saving
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepo.save(user);
    }

    public User update(String id, User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentRole = extractRoleFromUserDetails(userDetails);
        String targetRole = user.getRole().name();

        if (!isAuthorizedToManage(currentRole, targetRole))
            throw new RuntimeException("Unauthorized to update users");

        Optional<User> targetUserOpt = findById(id);
        targetUserOpt.orElseThrow(() -> new RuntimeException("User is not exists!"));

        user.setId(id);
        return userRepo.save(user);
    }

    public void delete(String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentRole = extractRoleFromUserDetails(userDetails);
        Optional<User> targetUserOpt = findById(id);

        User targetUser = targetUserOpt.orElseThrow(() -> new RuntimeException("User is not exists!"));

        if (!isAuthorizedToManage(currentRole, targetUser.getRole().name()))
            throw new RuntimeException("Unauthorized to delete user");

        userRepo.deleteById(id);
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(id);
    }

    public List<User> findAllByRole() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentRole = extractRoleFromUserDetails(userDetails);
        return userRepo.findAllByRoleStartingWith(getManagedRolePrefix(currentRole));
    }

    private String extractRoleFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getRole().name();
        }
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");
    }

    private boolean isAuthorizedToManage(String currentRole, String targetRole) {
        String[] manageableRoles = getManageableRoles(currentRole);
        return Arrays.stream(manageableRoles).anyMatch(role -> role.equals(targetRole));
    }

    private String[] getManageableRoles(String currentRole) {
        return switch (currentRole) {
            case "MOE_ADMIN" -> new String[]{"MOE_ADMIN", "MOE_EMPLOYEE", "PMOE_ADMIN"};
            case "PMOE_ADMIN" -> new String[]{"PMOE_ADMIN", "PMOE_EMPLOYEE", "ZMOE_ADMIN"};
            case "ZMOE_ADMIN" -> new String[]{"ZMOE_ADMIN", "ZMOE_EMPLOYEE", "SCHOOL_ADMIN"};
            case "SCHOOL_ADMIN" -> new String[]{"SCHOOL_ADMIN"};
            case "ADMIN" -> new String[]{"MOE_ADMIN", "MOE_EMPLOYEE", "PMOE_ADMIN", "PMOE_EMPLOYEE",
                    "ZMOE_ADMIN", "ZMOE_EMPLOYEE", "SCHOOL_ADMIN", "SCHOOL_EMPLOYEE"};
            default -> new String[]{};
        };
    }

    private String getManagedRolePrefix(String role) {
        return switch (role) {
            case "MOE_ADMIN" -> "MOE_";
            case "PMOE_ADMIN" -> "PMOE_";
            case "ZMOE_ADMIN" -> "ZMOE_";
            case "SCHOOL_ADMIN" -> "SCHOOL_";
            case "ADMIN" -> ""; // ADMIN can manage all
            default -> "";      // Employees or other roles have no prefix
        };
    }
}

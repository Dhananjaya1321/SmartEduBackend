package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.CustomUserDetails;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;


    public User save(User user) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Generate a random password if not provided or empty
        String generatedPassword = user.getPassword();
        if (generatedPassword == null || generatedPassword.isEmpty()) {
            generatedPassword = PasswordGeneratorUtil.generate();
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(generatedPassword));

        // Save user
        User savedUser = userRepo.save(user);

        // Send password email
        String subject = "Your SmartEdu Account Password";
        String message = "Hello,\n\nYour account has been created. Your temporary password is: " + generatedPassword +
                "\nPlease change it after your first login.\n\nRegards,\nSmartEdu Team";
        emailUtil.sendEmail(user.getEmail(), subject, message);

        return savedUser;
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


    public String checkEmailAndSendOTP(String email) {
        // Try finding user by email or username
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Incorrect email"));

        // Generate a 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Send password email
        String subject = "SmartEdu Password Reset OTP";
        String message = "Hello " + user.getUsername() + ",\n\n" +
                "You have requested to reset your password. Use the following OTP to proceed:\n\n" +
                "🔐 OTP: " + otp + "\n\n" +
                "Please do not share this code with anyone. It will expire soon for security reasons.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nSmartEdu Team";

        // Send the email
        emailUtil.sendEmail(user.getEmail(), subject, message);

        return otp;
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

    public String updatePassword(String email, String newPassword) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with given email");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        return "Password updated for " + email;
    }


}

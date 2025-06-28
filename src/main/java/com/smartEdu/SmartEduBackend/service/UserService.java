package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public User save(User user) {
        User save = userRepo.save(user);
        System.out.println(save);
        return user;
    }

}

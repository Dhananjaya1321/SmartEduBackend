package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService service;


    @PostMapping
    public User create(@RequestBody User user) {
        return service.save(user);
    }

}

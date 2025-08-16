package com.smartEdu.SmartEduBackend.util;

import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FindStudentByParentUsernameUtil {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ParentRepo parentRepo;

    public Student getStudent(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return student;
    }
}

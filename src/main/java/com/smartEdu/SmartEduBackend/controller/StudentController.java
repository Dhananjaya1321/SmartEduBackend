package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin // for frontend requests
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    @GetMapping("/{id}")
    public Optional<Student> getOne(@PathVariable String id) {
        return service.getStudentById(id);
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return service.createStudent(student);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable String id, @RequestBody Student student) {
        return service.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteStudent(id);
    }
}

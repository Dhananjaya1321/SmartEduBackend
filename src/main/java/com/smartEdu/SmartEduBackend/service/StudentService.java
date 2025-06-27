package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return repository.findById(id);
    }

    public Student createStudent(Student student) {
        return repository.save(student);
    }

    public Student updateStudent(String id, Student updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    public void deleteStudent(String id) {
        repository.deleteById(id);
    }
}

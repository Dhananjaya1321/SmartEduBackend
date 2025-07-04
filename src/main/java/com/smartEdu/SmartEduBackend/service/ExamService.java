package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Exam;
import com.smartEdu.SmartEduBackend.repo.ExamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepo examRepo;

    public Exam save(Exam exam) {
        return examRepo.save(exam);
    }

    public Exam update(String id, Exam exam) throws Exception {
        Optional<Exam> existing = examRepo.findById(id);
        if (existing.isPresent()) {
            exam.setId(id);
            return examRepo.save(exam);
        } else {
            throw new Exception("Exam not found!");
        }
    }

    public void delete(String id) throws Exception {
        if (!examRepo.existsById(id)) {
            throw new Exception("Exam not found!");
        }
        examRepo.deleteById(id);
    }

    public Optional<Exam> findById(String id) {
        return examRepo.findById(id);
    }

    public Page<Exam> findAll(int page, int size) {
        return examRepo.findAll(PageRequest.of(page, size));
    }

    public List<Exam> findByGrade(String grade) {
        return examRepo.findByGrade(grade);
    }

    public List<Exam> findByYear(int year) {
        return examRepo.findByYear(year);
    }
}

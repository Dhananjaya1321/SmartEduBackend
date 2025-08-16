package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private EventRepo eventRepo;

    public Event save(Event event, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        event.setSchoolId(institutionId);
        return eventRepo.save(event);
    }

    public Event update(String id, Event event) {
        Event existing = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found!"));
        event.setId(existing.getId()); // preserve ID
        event.setSchoolId(existing.getSchoolId()); // preserve ID
        return eventRepo.save(event);
    }

    public void delete(String id) {
        Event event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found!"));
        eventRepo.delete(event);
    }

    public Optional<Event> findById(String id) {
        eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found!"));
        return eventRepo.findById(id);
    }

    public List<Event> findAll(int page, int size, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);

        return eventRepo.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<Event> getEventsByGrade(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        String gradeId = student.getGradeId();

        Grades grades = gradesRepo.findById(gradeId).get();
        String gradeName = grades.getGradeName();
        List<String> matchingGrades = getMatchingGrades(gradeName);
        List<Event> eventList=new ArrayList<>();
        for (String g:matchingGrades){
            List<Event> byGrades = eventRepo.findByGrades(g);
            eventList.addAll(byGrades);
        }

        return eventList;
    }




    public static List<String> getMatchingGrades(String input) {
        // Parse the grade number from input (ignore stream if present)
        String numStr = input.contains("(") ? input.substring(0, input.indexOf("(")).trim() : input.trim();
        int gradeNum;
        try {
            gradeNum = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid grade input: " + input);
        }

        if (gradeNum < 1 || gradeNum > 13) {
            throw new IllegalArgumentException("Grade must be between 1 and 13: " + gradeNum);
        }

        List<String> matches = new ArrayList<>();
        for (GradeRange gr : GRADE_RANGES) {
            if (gradeNum >= gr.minGrade && gradeNum <= gr.maxGrade) {
                matches.add(gr.value);
            }
        }
        return matches;
    }

    static class GradeRange {
        String value;
        int minGrade;
        int maxGrade;

        public GradeRange(String value, int minGrade, int maxGrade) {
            this.value = value;
            this.minGrade = minGrade;
            this.maxGrade = maxGrade;
        }
    }

    private static final List<GradeRange> GRADE_RANGES = Arrays.asList(
            new GradeRange("grade_1", 1, 1),
            new GradeRange("grade_2", 2, 2),
            new GradeRange("grade_3", 3, 3),
            new GradeRange("grade_4", 4, 4),
            new GradeRange("grade_5", 5, 5),
            new GradeRange("grade_6", 6, 6),
            new GradeRange("grade_7", 7, 7),
            new GradeRange("grade_8", 8, 8),
            new GradeRange("grade_9", 9, 9),
            new GradeRange("grade_10", 10, 10),
            new GradeRange("grade_11", 11, 11),
            new GradeRange("grade_12", 12, 12),
            new GradeRange("grade_13", 13, 13),
            new GradeRange("grade_1_to_5", 1, 5),
            new GradeRange("grade_6_to_9", 6, 9),
            new GradeRange("grade_1_to_9", 1, 9),
            new GradeRange("grade_9_to_11", 9, 11),
            new GradeRange("grade_6_to_11", 6, 11),
            new GradeRange("grade_6_to_13", 6, 13),
            new GradeRange("grade_10_to_13", 10, 13),
            new GradeRange("grade_12_to_13", 12, 13),
            new GradeRange("all_grade", 1, 13)
    );
}

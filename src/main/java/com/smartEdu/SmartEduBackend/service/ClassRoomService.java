package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import com.smartEdu.SmartEduBackend.entity.Grades;
import com.smartEdu.SmartEduBackend.entity.Report;
import com.smartEdu.SmartEduBackend.entity.StudentReport;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ClassRoomService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private StudentReportRepo studentReportRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    public ClassRoom createClass(ClassRoom classRoom, String token) {
        ClassRoom save = classRoomRepo.save(classRoom);

        Grades grades = gradesRepo.findById(classRoom.getGradeId()).get();

        if (grades.getClassIds() == null) {
            grades.setClassIds(new ArrayList<>());
        }

        List<String> classIds = grades.getClassIds();
        classIds.add(save.getId());
        grades.setClassIds(classIds);

        gradesRepo.save(grades);
        return classRoomRepo.save(classRoom);
    }

    public List<ClassRoom> createMultipleClasses(List<ClassRoom> classRooms) {
        return classRoomRepo.saveAll(classRooms);
    }

    public List<ClassRoom> getAllClasses() {
        return classRoomRepo.findAll();
    }

    public ClassRoom getClassById(String id) {
        return classRoomRepo.findById(id).orElse(null);
    }

    public List<ClassRoom> getClassesByGrade(String grade) {
        return classRoomRepo.findByGradeId(grade);
    }

    public ClassRoom updateClass(String id, ClassRoom updated) {
        ClassRoom classRoom = classRoomRepo.findById(id).orElseThrow(() -> new RuntimeException("Class not found!"));
        classRoom.setClassName(updated.getClassName());
        classRoom.setClassTeacherId(updated.getClassTeacherId());
        return classRoomRepo.save(classRoom);
    }

    public void deleteClass(String id) {
        classRoomRepo.findById(id).orElseThrow(() -> new RuntimeException("Class not found!"));
        classRoomRepo.deleteById(id);
    }

    public boolean classesReshuffle(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);

        for (int gradeNum = 1; gradeNum <= 10; gradeNum++) {
            Grades grade = gradesRepo.findAllBySchoolIdAndGradeName(institutionId, String.valueOf(gradeNum));
            if (grade == null || grade.getClassIds() == null || grade.getClassIds().isEmpty()) {
                continue; // no classes in this grade
            }

            List<ClassRoom> classRooms = grade.getClassIds().stream()
                    .map(cid -> classRoomRepo.findById(cid).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            if (classRooms.isEmpty()) continue;

            // Compute minStudents based on valid students with last final exam report
            int minStudents = Integer.MAX_VALUE;
            Map<String, List<String>> validStudentsByClass = new HashMap<>();
            Map<String, List<String>> skippedStudentsByClass = new HashMap<>();

            String lastYear = String.valueOf(LocalDate.now().getYear() - 1);

            for (ClassRoom classRoom : classRooms) {
                List<String> validSids = new ArrayList<>();
                List<String> skipped = new ArrayList<>();

                for (String sid : classRoom.getStudentIds()) {
                    StudentReport studentReport = studentReportRepo.findByStudentId(sid);
                    if (studentReport == null) {
                        skipped.add(sid);
                        continue;
                    }

                    Report lastReport = studentReport.getReports().stream()
                            .filter(r -> r.getYear().equals(lastYear) && r.getExamName().equals("Final Term Exam"))
                            .findFirst()
                            .orElse(null);

                    if (lastReport == null || lastReport.getRank() <= 0) {
                        skipped.add(sid);
                        continue;
                    }

                    validSids.add(sid);
                }

                validStudentsByClass.put(classRoom.getId(), validSids);
                skippedStudentsByClass.put(classRoom.getId(), skipped);

                minStudents = Math.min(minStudents, validSids.size());
            }

            if (minStudents == 0) continue; // nothing to reshuffle

            // Now, for each class, sort valid students by rank asc, then by sid asc, take top minStudents, update skipped
            // rank -> list of students (but here rank is position 1 to minStudents)
            Map<Integer, List<String>> positionBuckets = new HashMap<>();

            for (ClassRoom classRoom : classRooms) {
                List<String> validSids = validStudentsByClass.get(classRoom.getId());
                List<String> skipped = skippedStudentsByClass.get(classRoom.getId());

                // Get reports for sorting
                List<Map.Entry<String, Report>> studentEntries = validSids.stream()
                        .map(sid -> {
                            StudentReport sr = studentReportRepo.findByStudentId(sid); // already fetched earlier, but to be safe
                            Report rep = sr.getReports().stream()
                                    .filter(r -> r.getYear().equals(lastYear) && r.getExamName().equals("Final Term Exam"))
                                    .findFirst()
                                    .orElse(null); // safer than .get() to avoid NoSuchElementException
                            if (rep == null) {
                                return null; // handle null case
                            }
                            return Map.entry(sid, rep); // use Map.entry for Map.Entry compliance
                        })
                        .filter(Objects::nonNull) // filter out null entries
                        .sorted(Comparator.comparingInt((Map.Entry<String, Report> e) -> e.getValue().getRank())
                                .thenComparing(Map.Entry::getKey))
                        .toList();

                // Take top minStudents
                List<String> topSids = studentEntries.subList(0, minStudents).stream()
                        .map(Map.Entry::getKey)
                        .toList();

                // The remaining valid go to skipped
                List<String> remainingValid = studentEntries.subList(minStudents, studentEntries.size()).stream()
                        .map(Map.Entry::getKey)
                        .toList();
                skipped.addAll(remainingValid);

                // Now, add to position buckets (1-based)
                for (int pos = 0; pos < minStudents; pos++) {
                    String sid = topSids.get(pos);
                    positionBuckets.computeIfAbsent(pos + 1, k -> new ArrayList<>()).add(sid);
                }
            }

            // Prepare new distribution map
            Map<String, List<String>> newDistribution = new HashMap<>();
            for (ClassRoom cr : classRooms) {
                newDistribution.put(cr.getId(), new ArrayList<>());
            }

            // Distribute students position-wise (1..minStudents)
            for (int pos = 1; pos <= minStudents; pos++) {
                List<String> sameLevelStudents = positionBuckets.getOrDefault(pos, new ArrayList<>());
                Collections.shuffle(sameLevelStudents); // randomize order

                int idx = 0;
                for (ClassRoom classRoom : classRooms) {
                    if (idx < sameLevelStudents.size()) {
                        String sid = sameLevelStudents.get(idx);
                        newDistribution.get(classRoom.getId()).add(sid);
                    }
                    idx++;
                }
            }

            // Update classes + students
            for (ClassRoom classRoom : classRooms) {
                List<String> finalStudents = new ArrayList<>();
                finalStudents.addAll(newDistribution.getOrDefault(classRoom.getId(), List.of())); // reshuffled
                finalStudents.addAll(skippedStudentsByClass.getOrDefault(classRoom.getId(), List.of())); // skipped remain

                classRoom.setStudentIds(finalStudents);
                classRoomRepo.save(classRoom);

                // Update each student to point to the correct classId
                for (String sid : finalStudents) {
                    studentRepo.findById(sid).ifPresent(student -> {
                        student.setClassId(classRoom.getId());
                        studentRepo.save(student);
                        System.out.println("✅ Student " + sid + " moved to class " + classRoom.getId());
                    });
                }
            }
        }
        return true;
    }

}

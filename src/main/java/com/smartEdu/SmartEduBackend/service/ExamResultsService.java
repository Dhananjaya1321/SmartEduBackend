package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ExamResultsService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    @Autowired
    private ExamResultsRepo examResultsRepo;

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private SubjectResultsRepo subjectResultsRepo;

    @Autowired
    private StudentReportRepo studentReportRepo;

    @Autowired
    private StudentRepo studentRepo;

    public SubjectResults save(SubjectResults exam, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        exam.setSchoolId(institutionId);
        return subjectResultsRepo.save(exam);
    }

    public ExamResults saveExamResults(ExamResults examResults, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).orElseThrow();
        ClassRoom classRoom = classRoomRepo.findByClassTeacherId(user.getProfileId());
        Grades grade = gradesRepo.findByGradeNameAndSchoolId(examResults.getGradeName(), institutionId);

        examResults.setExamResultsStatus(ExamsResults.RELEASED);
        examResults.setClassId(classRoom.getId());
        examResults.setGradeId(grade.getId());

        Exam exam = examRepo.findById(examResults.getExamId()).orElseThrow();

        List<StudentReport> studentReports = new ArrayList<>();

        // Step 1: Calculate totals for each student
        for (String studentId : classRoom.getStudentIds()) {
            Student student = studentRepo.findById(studentId).orElseThrow();
            StudentReport studentReport = studentReportRepo.findByStudentId(student.getId());

            // Build new report for this exam
            Report report = Report.builder()
                    .year(examResults.getYear())
                    .examId(examResults.getExamId())
                    .examName(examResults.getExamName())
                    .gradeId(examResults.getGradeId())
                    .gradeName(examResults.getGradeName())
                    .build();

            List<Marks> marksList = new ArrayList<>();
            double totalMarks = 0.0;
            int subjectCount = 0;

            for (ExamTimetableEntry ete : exam.getTimetable()) {
                String subject = ete.getSubject();
                double marks = 0;

                SubjectResults subjectResults = subjectResultsRepo.findByExamIdAndSubjectAndGradeIdAndSchoolIdAndClassIdAndYear(
                        exam.getId(), subject, examResults.getGradeId(), institutionId, classRoom.getId(), exam.getYear()
                );

                if (subjectResults != null) {
                    for (StudentWithSubjectMarks swsm : subjectResults.getStudents()) {
                        if (swsm.getStudentId().equals(student.getId())) {
                            marks = swsm.getMarks();
                            break;
                        }
                    }
                }

                marksList.add(Marks.builder()
                        .subject(subject)
                        .marks(marks)
                        .build());

                totalMarks += marks;
                subjectCount++;
            }

            double averageMarks = subjectCount > 0 ? totalMarks / subjectCount : 0.0;

            report.setMarksList(marksList);
            report.setTotalMarks(totalMarks);
            report.setAverageMarks(averageMarks);

            if (studentReport == null) {
                // Create new report
                studentReport = StudentReport.builder()
                        .studentId(student.getId())
                        .studentName(student.getFullNameWithInitials())
                        .schoolId(institutionId)
                        .reports(new ArrayList<>(List.of(report)))
                        .build();
            } else {
                // Update existing report
                List<Report> reports = studentReport.getReports();
                if (reports == null) {
                    reports = new ArrayList<>();
                }

                // Remove old report for the same exam (avoid duplicates)
                reports.removeIf(r -> r.getExamId().equals(examResults.getExamId()));

                reports.add(report);
                studentReport.setReports(reports);
            }

            studentReports.add(studentReport);
        }

        // Step 2: Assign ranks
        List<Report> reportsForRanking = studentReports.stream()
                .map(sr -> sr.getReports().stream()
                        .filter(r -> r.getExamId().equals(examResults.getExamId()))
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(Report::getTotalMarks).reversed())
                .toList();

        int rank = 1;
        double previousMarks = -1;
        int studentsWithSameMarks = 0;

        for (int i = 0; i < reportsForRanking.size(); i++) {
            Report r = reportsForRanking.get(i);

            if (r.getTotalMarks() == previousMarks) {
                // Same marks = same rank
                r.setRank(rank);
                studentsWithSameMarks++;
            } else {
                // Different marks = update rank
                rank = rank + studentsWithSameMarks;
                r.setRank(rank);
                studentsWithSameMarks = 1;
            }
            previousMarks = r.getTotalMarks();
        }

        // Step 3: Save back reports
        for (StudentReport sr : studentReports) {
            studentReportRepo.save(sr);
        }

        return examResultsRepo.save(examResults);
    }

    public List<ReportResponse> getAllClassStudentsResultsDetails(String examId, String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).orElseThrow();
        ClassRoom classRoom = classRoomRepo.findByClassTeacherId(user.getProfileId());

        List<ReportResponse> reportResponses = new ArrayList<>();
        for (String studentId : classRoom.getStudentIds()) {
            StudentReport studentReport = studentReportRepo.findByStudentId(studentId);
            for (Report r : studentReport.getReports()) {
                if (r.getExamId().equals(examId)) {
                    reportResponses.add(
                            ReportResponse.builder()
                                    .studentId(studentId)
                                    .studentName(studentReport.getStudentName())
                                    .year(r.getYear())
                                    .examId(r.getExamId())
                                    .examName(r.getExamName())
                                    .gradeId(r.getGradeId())
                                    .gradeName(r.getGradeName())
                                    .totalMarks(r.getTotalMarks())
                                    .averageMarks(r.getAverageMarks())
                                    .rank(r.getRank())
                                    .marksList(r.getMarksList())
                                    .build()
                    );
                }
            }
        }
        return reportResponses;
    }

    public StudentReport getStudentsResultsDetailsToTeacher(String studentId, String token) {
        String year = String.valueOf(LocalDate.now().getYear());

        StudentReport studentReport = studentReportRepo.findByStudentId(studentId);
        List<Report> reports = new ArrayList<>();
        for (Report r : studentReport.getReports()) {
            if (r.getYear().equals(year) && r.getExamName().equals("First Term Exam")) {
                reports.add(r);
            }else if (r.getYear().equals(year) && r.getExamName().equals("Mid-Term Exam")) {
                reports.add(r);
            }else if (r.getYear().equals(year) && r.getExamName().equals("Final Term Exam")) {
                reports.add(r);
            }
        }
        studentReport.setReports(reports);
        return studentReport;
    }

    public StudentReport getStudentsResultsDetailsToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();

        String year = String.valueOf(LocalDate.now().getYear());

        StudentReport studentReport = studentReportRepo.findByStudentId(student.getId());
        List<Report> reports = new ArrayList<>();
        for (Report r : studentReport.getReports()) {
            if (r.getYear().equals(year) && r.getExamName().equals("First Term Exam")) {
                reports.add(r);
            }
            if (r.getYear().equals(year) && r.getExamName().equals("Mid-Term Exam")) {
                reports.add(r);
            }
            if (r.getYear().equals(year) && r.getExamName().equals("Final Term Exam")) {
                reports.add(r);
            }
        }
        studentReport.setReports(reports);
        return studentReport;
    }
}

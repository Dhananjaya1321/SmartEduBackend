package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
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
public class DashboardService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Autowired
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Autowired
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    public Dashboard getSchoolDashboardDetails(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        int staffUsersCount = userRepo.countAllByInstitutionIDAndRole(institutionId, Role.SCHOOL_EMPLOYEE);
        int teachersCount = userRepo.countAllByInstitutionIDAndRole(institutionId, Role.TEACHER);
        int studentsCount = studentRepo.countBySchoolId(institutionId);

        return new Dashboard().builder()
                .studentsCount(studentsCount)
                .parentsCount(studentsCount)
                .teachersCount(teachersCount)
                .staffUsersCount(staffUsersCount + 1)
                .build();
    }

    public Dashboard getZonalDashboardDetails(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        int staffUsersCount = userRepo.countAllByInstitutionIDAndRole(institutionId, Role.ZMOE_EMPLOYEE);
        int teachersCount = 0, studentsCount = 0;
        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).get();

        for (String s : zonalEducationOffice.getSchoolsIds()) {
            teachersCount += userRepo.countAllByInstitutionIDAndRole(s, Role.TEACHER);
            studentsCount += studentRepo.countBySchoolId(s);
        }


        return new Dashboard().builder()
                .studentsCount(studentsCount)
                .parentsCount(studentsCount)
                .teachersCount(teachersCount)
                .principalsCount(zonalEducationOffice.getSchoolsIds().size())
                .schoolsCount(zonalEducationOffice.getSchoolsIds().size())
                .staffUsersCount(staffUsersCount + 1)
                .build();
    }

    public Dashboard getProvinceDashboardDetails(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        int staffUsersCount = userRepo.countAllByInstitutionIDAndRole(institutionId, Role.PMOE_EMPLOYEE);
        int teachersCount = 0, studentsCount = 0, schoolsCount = 0;


        ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findById(institutionId).get();
        List<ZonalEducationOffice> zonalOffices = provincialEducationOffice.getZonalOffices();

        for (ZonalEducationOffice z : zonalOffices) {
            ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(z.getId()).get();
            schoolsCount += zonalEducationOffice.getSchoolsIds().size();
            for (String s : zonalEducationOffice.getSchoolsIds()) {
                teachersCount += userRepo.countAllByInstitutionIDAndRole(s, Role.TEACHER);
                studentsCount += studentRepo.countBySchoolId(s);
            }
        }


        return new Dashboard().builder()
                .studentsCount(studentsCount)
                .parentsCount(studentsCount)
                .teachersCount(teachersCount)
                .principalsCount(schoolsCount)
                .schoolsCount(schoolsCount)
                .zonalCount(zonalOffices.size())
                .staffUsersCount(staffUsersCount + 1)
                .build();
    }

    public Dashboard getDetailsToMOEDashboard(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        int staffUsersCount = userRepo.countAllByInstitutionIDAndRole(institutionId, Role.MOE_EMPLOYEE);
        int teachersCount = 0, studentsCount = 0, schoolsCount = 0, zonalCount = 0;

        MinistryOfEducationOffice ministry = ministryEducationOfficeRepo.findById(institutionId).get();
        List<ProvincialEducationOffice> provincialOffices = ministry.getProvincialOffices();


        for (ProvincialEducationOffice p : provincialOffices) {
            ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findById(p.getId()).get();
            List<ZonalEducationOffice> zonalOffices = provincialEducationOffice.getZonalOffices();
            zonalCount += zonalOffices.size();
            for (ZonalEducationOffice z : zonalOffices) {
                ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(z.getId()).get();
                schoolsCount += zonalEducationOffice.getSchoolsIds().size();
                for (String s : zonalEducationOffice.getSchoolsIds()) {
                    teachersCount += userRepo.countAllByInstitutionIDAndRole(s, Role.TEACHER);
                    studentsCount += studentRepo.countBySchoolId(s);
                }
            }
        }


        return new Dashboard().builder()
                .studentsCount(studentsCount)
                .parentsCount(studentsCount)
                .teachersCount(teachersCount)
                .principalsCount(schoolsCount)
                .schoolsCount(schoolsCount)
                .zonalCount(zonalCount)
                .provinceCount(ministry.getProvincialOffices().size())
                .staffUsersCount(staffUsersCount + 1)
                .build();
    }
}

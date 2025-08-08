package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ExamsAndNICApplication;
import com.smartEdu.SmartEduBackend.enums.ExamsAndNICApplicationStatus;
import com.smartEdu.SmartEduBackend.repo.ExamsAndNICApplicationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExamsAndNICApplicationService {

    @Autowired
    private ExamsAndNICApplicationRepo examsAndNICApplicationRepo;

    public ExamsAndNICApplication save(ExamsAndNICApplication examsAndNICApplication) {
        examsAndNICApplication.setStatus(ExamsAndNICApplicationStatus.PENDING);
        return examsAndNICApplicationRepo.save(examsAndNICApplication);
    }
}

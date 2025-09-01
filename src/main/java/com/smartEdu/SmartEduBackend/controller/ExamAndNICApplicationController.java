package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ExamsAndNICApplication;
import com.smartEdu.SmartEduBackend.service.ExamsAndNICApplicationService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams-and-nic-applications")
@CrossOrigin
public class ExamAndNICApplicationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamAndNICApplicationController.class);

    @Autowired
    private ExamsAndNICApplicationService examsAndNICApplicationService;

    @PostMapping
    public ResponseEntity<ResponseUtil> save(@RequestBody ExamsAndNICApplication examsAndNICApplication) {
        System.out.println(examsAndNICApplication);
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exam saved successfully.",
                            examsAndNICApplicationService.save(examsAndNICApplication)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}

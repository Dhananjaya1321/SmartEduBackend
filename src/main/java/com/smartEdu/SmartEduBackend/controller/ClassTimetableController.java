package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ClassTimetable;
import com.smartEdu.SmartEduBackend.service.ClassTimetableService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetables")
@CrossOrigin
public class ClassTimetableController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassTimetableController.class);

    @Autowired
    private ClassTimetableService service;

    @PostMapping
    public ResponseEntity<ResponseUtil> save(
            @RequestBody ClassTimetable timetable,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Timetable saved successfully.",
                            service.save(timetable, token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Timetable is already exists!"))
                    return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody ClassTimetable timetable) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Timetable updated successfully.", service.update(id, timetable)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Timetable not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Timetable deleted successfully.", null));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Timetable loaded.", service.findById(id).orElse(null)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ResponseUtil> findByClassId(@PathVariable String classId) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Timetable loaded.", service.findByClassId(classId).orElse(null)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseUtil> findAll() {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "All timetables loaded.", service.findAll()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/by-gradeId/{gradeId}")
    public ResponseEntity<ResponseUtil> findAllTimetablesByGradeId(@PathVariable String gradeId) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "All timetables loaded.", service.findAllTimetablesByGradeId(gradeId)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}

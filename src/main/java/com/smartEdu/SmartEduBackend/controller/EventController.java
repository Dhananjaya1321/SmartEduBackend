package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Event;
import com.smartEdu.SmartEduBackend.service.EventService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<ResponseUtil> save(@RequestBody Event event,@RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Event created successfully", eventService.save(event,token)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody Event event) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Event updated successfully", eventService.update(id, event)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Event not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            eventService.delete(id);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Event deleted successfully", null));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Event not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Event retrieved successfully", eventService.findById(id).orElse(null)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Event not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseUtil> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Events retrieved successfully", eventService.findAll(page, size,token)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/by-grade")
    public ResponseEntity<ResponseUtil> getEventsByGrade(@RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<Event> events = eventService.getEventsByGrade(token);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Events fetched successfully.", events));
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

}

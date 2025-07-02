package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Event;
import com.smartEdu.SmartEduBackend.repo.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    public Event save(Event event) {
        return eventRepo.save(event);
    }

    public Event update(String id, Event event) {
        Event existing = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found!"));
        event.setId(existing.getId()); // preserve ID
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

    public List<Event> findAll(int page, int size) {
        return eventRepo.findAll(PageRequest.of(page, size)).getContent();
    }
}

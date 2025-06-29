package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrincipalService {

    @Autowired
    private PrincipalRepo principalRepo;

    public Principal save(Principal principal) {
        return principalRepo.save(principal);
    }

    public Principal update(String id, Principal updatedPrincipal) {
        principalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Principal not found!"));

        updatedPrincipal.setId(id);
        return principalRepo.save(updatedPrincipal);
    }

    public void delete(String id) {
        principalRepo.findById(id).orElseThrow(() -> new RuntimeException("Principal not found!"));
        principalRepo.deleteById(id);
    }

    public Optional<Principal> findById(String id) {
        return principalRepo.findById(id);
    }

    public Page<Principal> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return principalRepo.findAll(pageable);
    }
}

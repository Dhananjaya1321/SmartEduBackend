package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByRoleStartingWithAndInstitutionID(String managedRolePrefix,String institutionID);

    List<User> findAllByRole(Pageable pageable, Role role);

    long countByRole(Role role);

    int deleteByInstitutionIDAndRole(String institutionID,Role role);

    User findByProfileId(String id);

}

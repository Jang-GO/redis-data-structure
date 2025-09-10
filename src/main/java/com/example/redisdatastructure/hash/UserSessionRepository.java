package com.example.redisdatastructure.hash;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserSessionRepository extends CrudRepository<UserSession, String> {
    Optional<UserSession> findByUserId(String userId);
}

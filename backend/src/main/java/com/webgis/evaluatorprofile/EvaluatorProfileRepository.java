package com.webgis.evaluatorprofile;

import com.webgis.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluatorProfileRepository extends JpaRepository<EvaluatorProfile, Long> {
    Optional<EvaluatorProfile> findByUser(User user);
    boolean existsByUser(User user);
}
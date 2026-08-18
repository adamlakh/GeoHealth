package com.webgis.map.finalmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinalMapRepository extends JpaRepository<FinalMap, Long> {
    Optional<FinalMap> findByTitle(String title);
    Optional<FinalMap> findById(long id);
}

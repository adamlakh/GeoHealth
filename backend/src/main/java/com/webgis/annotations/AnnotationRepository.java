package com.webgis.annotations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnnotationRepository extends JpaRepository<Annotation, AnnotationId> {
    Optional<Annotation> findByAnnotationId(AnnotationId annotationId);

    // find all annotations for a map by a user
    @Query("SELECT a FROM Annotation a WHERE a.annotationId.mapId = :mapId AND a.annotationId.userId = :userId")
    Optional<Annotation> findByMapIdAndUserId(@Param("mapId") Long mapId, @Param("userId") Long userId);
}
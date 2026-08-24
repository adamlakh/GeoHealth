package com.webgis.annotations;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnnotationService {
    private final AnnotationRepository annotationRepository;

    public AnnotationService(AnnotationRepository annotationRepository){this.annotationRepository = annotationRepository;}

    /**
     * Search for annotations db using their identifier
     *
     * @param annotationId identifier of the annotations you want to retrieve from the db
     * @return annotations corresponding to the identifier, empty otherwise
     */
    public Optional<Annotation> findByAnnotationId(AnnotationId annotationId){
        return annotationRepository.findByAnnotationId(annotationId);
    }

    public Optional<Annotation> findByMapIdAndUserId(Long mapId, Long userId) {
        return annotationRepository.findByMapIdAndUserId(mapId, userId);
    }

    /**
     * Save the annotation (geojson + AnnotationId) in the db
     *
     * @param annotation : the annotations you want to save
     * @return Saved annotation instance
     * */
    public Annotation save(Annotation annotation){
        return annotationRepository.save(annotation);
    }
}

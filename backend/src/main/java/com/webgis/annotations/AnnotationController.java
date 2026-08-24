package com.webgis.annotations;

import com.webgis.MessageDto;
import com.webgis.annotations.dto.AnnotationDTO;
import com.webgis.annotations.dto.AnnotationPostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;

@RestController
@RequestMapping("/annotation")
public class AnnotationController {
    final private AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService){this.annotationService  = annotationService;}

    @GetMapping("/{mapId}/{userId}")
    public ResponseEntity<Object> getAnnotation(@PathVariable Long mapId,
                                                @PathVariable Long userId
                                                ){

        final Optional<Annotation> annotation = annotationService.findByMapIdAndUserId(mapId, userId);
        if (annotation.isPresent()){
            return ResponseEntity.status(200).body(new AnnotationDTO( annotation.get().getGeoJson()));
        }else {
            return ResponseEntity.status(400).body("Geojson for annotations not found");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Object> postAnnotation(@RequestBody AnnotationPostDTO dto) {
        try {
            final Long mapId = dto.mapId();
            final Long userId = dto.userId();
            final String geoJson = dto.geoJson();

            final AnnotationId id = new AnnotationId(userId, mapId);

            final Annotation annotation = new Annotation(id, geoJson);

            return ResponseEntity.status(200).body(annotationService.save(annotation));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(e.getMessage()));
        }
    }

}

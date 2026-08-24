package com.webgis.annotations;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class AnnotationId implements Serializable  {
    private Long userId;
    private Long mapId;

    public AnnotationId(Long userId, Long mapId) {
        this.userId = userId;
        this.mapId= mapId;
    }

    public AnnotationId(){}

    public Long getMapId(){return this.mapId;}
    public Long getUserId(){return this.userId;}

    public void setMapId(Long mapId){this.mapId = mapId;}
    public void setUserId(Long userId){this.userId = userId;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotationId)) return false;

        final AnnotationId annotationId = (AnnotationId) o;

        if (mapId != annotationId.mapId) return false;
        return userId.equals(annotationId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, mapId);
    }
}

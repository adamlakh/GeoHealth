package com.webgis.annotations.dto;

/**
 * Data Transfer Object containing fields for sending annotation to backend for saving purpose
 */
public record AnnotationPostDTO(Long mapId, Long userId, String geoJson) {}

package com.webgis.evaluatorprofile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveDiseaseExperienceDto(
        @NotNull @Min(1) @Max(10) Integer pathogenKnowledgeScore,
        @NotNull @Min(1) @Max(10) Integer transmissionKnowledgeScore,
        @NotNull @Min(1) @Max(10) Integer animalClinicalKnowledgeScore,
        @NotNull @Min(1) @Max(10) Integer humanClinicalKnowledgeScore,
        @NotNull Boolean professionallyExposed,
        @NotBlank String exposureFrequency,
        @NotBlank String yearsInvolved
) {}
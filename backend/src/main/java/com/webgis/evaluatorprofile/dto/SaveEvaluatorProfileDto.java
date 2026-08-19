package com.webgis.evaluatorprofile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveEvaluatorProfileDto(
        @NotEmpty List<String> professions,
        @NotEmpty List<String> sectors,
        @NotEmpty List<String> interventionLevels,
        @NotEmpty List<String> sectorsWorkedIn,
        @NotBlank String countries,
        @NotBlank String regions,
        @NotBlank String divisions,
        @NotNull @Valid SaveDiseaseExperienceDto rvfExperience,
        @NotNull @Valid SaveDiseaseExperienceDto evdExperience
) {}
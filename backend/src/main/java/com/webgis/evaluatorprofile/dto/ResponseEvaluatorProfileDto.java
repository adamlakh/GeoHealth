package com.webgis.evaluatorprofile.dto;

import com.webgis.evaluatorprofile.EvaluatorProfile;

import java.util.List;

public record ResponseEvaluatorProfileDto(
        Long id,
        List<String> professions,
        List<String> sectors,
        List<String> interventionLevels,
        List<String> sectorsWorkedIn,
        String countries,
        String regions,
        String divisions,
        ResponseDiseaseExperienceDto rvfExperience,
        ResponseDiseaseExperienceDto evdExperience
) {
    public ResponseEvaluatorProfileDto(EvaluatorProfile evaluatorProfile) {
        this(
                evaluatorProfile.getId(),
                evaluatorProfile.getProfessions(),
                evaluatorProfile.getSectors(),
                evaluatorProfile.getInterventionLevels(),
                evaluatorProfile.getSectorsWorkedIn(),
                evaluatorProfile.getCountries(),
                evaluatorProfile.getRegions(),
                evaluatorProfile.getDivisions(),
                new ResponseDiseaseExperienceDto(evaluatorProfile.getRvfExperience()),
                new ResponseDiseaseExperienceDto(evaluatorProfile.getEvdExperience())
        );
    }
}
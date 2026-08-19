package com.webgis.evaluatorprofile.dto;

import com.webgis.evaluatorprofile.DiseaseExperience;

public record ResponseDiseaseExperienceDto(
        Integer pathogenKnowledgeScore,
        Integer transmissionKnowledgeScore,
        Integer animalClinicalKnowledgeScore,
        Integer humanClinicalKnowledgeScore,
        boolean professionallyExposed,
        String exposureFrequency,
        String yearsInvolved
) {
    public ResponseDiseaseExperienceDto(DiseaseExperience diseaseExperience) {
        this(
                diseaseExperience.getPathogenKnowledgeScore(),
                diseaseExperience.getTransmissionKnowledgeScore(),
                diseaseExperience.getAnimalClinicalKnowledgeScore(),
                diseaseExperience.getHumanClinicalKnowledgeScore(),
                diseaseExperience.isProfessionallyExposed(),
                diseaseExperience.getExposureFrequency(),
                diseaseExperience.getYearsInvolved()
        );
    }
}
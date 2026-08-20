package com.webgis.evaluatorprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DiseaseExperience {
    @Column
    private Integer pathogenKnowledgeScore;

    @Column
    private Integer transmissionKnowledgeScore;

    @Column
    private Integer animalClinicalKnowledgeScore;

    @Column
    private Integer humanClinicalKnowledgeScore;

    @Column
    private boolean professionallyExposed;

    @Column
    private String exposureFrequency;

    @Column
    private String yearsInvolved;


    public DiseaseExperience() {}

    public DiseaseExperience(Integer pathogenKnowledgeScore,
                             Integer transmissionKnowledgeScore,
                             Integer animalClinicalKnowledgeScore,
                             Integer humanClinicalKnowledgeScore,
                             boolean professionallyExposed,
                             String exposureFrequency,
                             String yearsInvolved) {
        this.pathogenKnowledgeScore = pathogenKnowledgeScore;
        this.transmissionKnowledgeScore = transmissionKnowledgeScore;
        this.animalClinicalKnowledgeScore = animalClinicalKnowledgeScore;
        this.humanClinicalKnowledgeScore = humanClinicalKnowledgeScore;
        this.professionallyExposed = professionallyExposed;
        this.exposureFrequency = exposureFrequency;
        this.yearsInvolved = yearsInvolved;
    }


    public Integer getPathogenKnowledgeScore() { return pathogenKnowledgeScore; }
    public Integer getTransmissionKnowledgeScore() { return transmissionKnowledgeScore; }
    public Integer getAnimalClinicalKnowledgeScore() { return animalClinicalKnowledgeScore; }
    public Integer getHumanClinicalKnowledgeScore() { return humanClinicalKnowledgeScore; }
    public boolean isProfessionallyExposed() { return professionallyExposed; }
    public String getExposureFrequency() { return exposureFrequency; }
    public String getYearsInvolved() { return yearsInvolved; }

}

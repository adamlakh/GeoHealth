package com.webgis.evaluatorprofile;

import com.webgis.user.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "evaluatorProfiles")
public class EvaluatorProfile implements AutoCloseable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ElementCollection
    @Column(name = "profession")
    private List<String> professions;

    @ElementCollection
    @Column(name = "sector")
    private List<String> sectors;

    @ElementCollection
    @Column(name = "intervention_level")
    private List<String> interventionLevels;

    @ElementCollection
    @Column(name = "sector_worked_in")
    private List<String> sectorsWorkedIn;

    @Column(columnDefinition = "TEXT")
    private String countries;

    @Column(columnDefinition = "TEXT")
    private String regions;

    @Column(columnDefinition = "TEXT")
    private String divisions;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pathogenKnowledgeScore", column = @Column(name = "rvf_pathogen_knowledge_score")),
            @AttributeOverride(name = "transmissionKnowledgeScore", column = @Column(name = "rvf_transmission_knowledge_score")),
            @AttributeOverride(name = "animalClinicalKnowledgeScore", column = @Column(name = "rvf_animal_clinical_knowledge_score")),
            @AttributeOverride(name = "humanClinicalKnowledgeScore", column = @Column(name = "rvf_human_clinical_knowledge_score")),
            @AttributeOverride(name = "professionallyExposed", column = @Column(name = "rvf_professionally_exposed")),
            @AttributeOverride(name = "exposureFrequency", column = @Column(name = "rvf_exposure_frequency")),
            @AttributeOverride(name = "yearsInvolved", column = @Column(name = "rvf_years_involved"))
    })
    private DiseaseExperience rvfExperience;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pathogenKnowledgeScore", column = @Column(name = "evd_pathogen_knowledge_score")),
            @AttributeOverride(name = "transmissionKnowledgeScore", column = @Column(name = "evd_transmission_knowledge_score")),
            @AttributeOverride(name = "animalClinicalKnowledgeScore", column = @Column(name = "evd_animal_clinical_knowledge_score")),
            @AttributeOverride(name = "humanClinicalKnowledgeScore", column = @Column(name = "evd_human_clinical_knowledge_score")),
            @AttributeOverride(name = "professionallyExposed", column = @Column(name = "evd_professionally_exposed")),
            @AttributeOverride(name = "exposureFrequency", column = @Column(name = "evd_exposure_frequency")),
            @AttributeOverride(name = "yearsInvolved", column = @Column(name = "evd_years_involved"))
    })
    private DiseaseExperience evdExperience;


    public EvaluatorProfile() {}

    public EvaluatorProfile(User user,
                            List<String> professions,
                            List<String> sectors,
                            List<String> interventionLevels,
                            List<String> sectorsWorkedIn,
                            String countries,
                            String regions,
                            String divisions,
                            DiseaseExperience rvfExperience,
                            DiseaseExperience evdExperience) {
        this.user = user;
        this.professions = professions;
        this.sectors = sectors;
        this.interventionLevels = interventionLevels;
        this.sectorsWorkedIn = sectorsWorkedIn;
        this.countries = countries;
        this.regions = regions;
        this.divisions = divisions;
        this.rvfExperience = rvfExperience;
        this.evdExperience = evdExperience;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public List<String> getProfessions() { return professions; }
    public List<String> getSectors() { return sectors; }
    public List<String> getInterventionLevels() { return interventionLevels; }
    public List<String> getSectorsWorkedIn() { return sectorsWorkedIn; }
    public String getCountries() { return countries; }
    public String getRegions() { return regions; }
    public String getDivisions() { return divisions; }
    public DiseaseExperience getRvfExperience() { return rvfExperience; }
    public DiseaseExperience getEvdExperience() { return evdExperience; }
}

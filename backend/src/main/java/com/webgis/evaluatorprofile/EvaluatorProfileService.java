package com.webgis.evaluatorprofile;


import com.webgis.evaluatorprofile.dto.SaveDiseaseExperienceDto;
import com.webgis.evaluatorprofile.dto.SaveEvaluatorProfileDto;
import com.webgis.user.User;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EvaluatorProfileService {

    private final EvaluatorProfileRepository evaluatorProfileRepository;

    public EvaluatorProfileService(EvaluatorProfileRepository evaluatorProfileRepository) {
        this.evaluatorProfileRepository = evaluatorProfileRepository;
    }

    /**
     * Saves a new evaluator profile for the given user.
     *
     * @param dto  The data transfer object containing the profile information.
     * @param user The user for whom the profile is being created.
     * @return The saved EvaluatorProfile entity.
     * @throws IllegalArgumentException if the user already has a profile.
     */
    public EvaluatorProfile saveProfile(SaveEvaluatorProfileDto dto, User user) {
        if (hasProfile(user)) {
            throw new IllegalArgumentException("User already has a profile");
        }

        final DiseaseExperience rvfExperience = toDiseaseExperience(dto.rvfExperience());
        final DiseaseExperience evdExperience = toDiseaseExperience(dto.evdExperience());

        final EvaluatorProfile evaluatorProfile = new EvaluatorProfile(
                user,
                dto.professions(),
                dto.sectors(),
                dto.interventionLevels(),
                dto.sectorsWorkedIn(),
                dto.countries(),
                dto.regions(),
                dto.divisions(),
                rvfExperience,
                evdExperience
        );
        return evaluatorProfileRepository.save(evaluatorProfile);
    }

    /**
     * Checks if the given user already has an evaluator profile.
     *
     * @param user The user to check for an existing profile.
     * @return true if the user has a profile, false otherwise.
     */
    public boolean hasProfile(User user) {
        return evaluatorProfileRepository.existsByUser(user);
    }

    /**
     * Retrieves the evaluator profile for the given user, if it exists.
     *
     * @param user The user whose profile is to be retrieved.
     * @return An Optional containing the EvaluatorProfile if found, or empty if not found.
     */
    public Optional<EvaluatorProfile> getProfileForUser(User user) {
        return evaluatorProfileRepository.findByUser(user);
    }

    /**
     * Converts a SaveDiseaseExperienceDto to a DiseaseExperience entity.
     *
     * @param dto The data transfer object containing disease experience information.
     * @return A DiseaseExperience entity constructed from the DTO.
     */
    private DiseaseExperience toDiseaseExperience(SaveDiseaseExperienceDto dto) {
        return new DiseaseExperience(
                dto.pathogenKnowledgeScore(),
                dto.transmissionKnowledgeScore(),
                dto.animalClinicalKnowledgeScore(),
                dto.humanClinicalKnowledgeScore(),
                dto.professionallyExposed(),
                dto.exposureFrequency(),
                dto.yearsInvolved()
        );
    }


    /**
     * Updates the existing evaluator profile for the given user.
     *
     * @param dto  The data transfer object containing the updated profile information.
     * @param user The user whose profile is being updated.
     * @return The updated EvaluatorProfile entity.
     * @throws IllegalArgumentException if the user has no existing profile.
     */
    public EvaluatorProfile updateProfile(SaveEvaluatorProfileDto dto, User user) {
        final Optional<EvaluatorProfile> optionalProfile = getProfileForUser(user);
        if (optionalProfile.isEmpty()) {
            throw new IllegalArgumentException("User does not have a profile to update");
        }

        final EvaluatorProfile evaluatorProfile = optionalProfile.get();

        evaluatorProfile.setProfessions(dto.professions());
        evaluatorProfile.setSectors(dto.sectors());
        evaluatorProfile.setInterventionLevels(dto.interventionLevels());
        evaluatorProfile.setSectorsWorkedIn(dto.sectorsWorkedIn());
        evaluatorProfile.setCountries(dto.countries());
        evaluatorProfile.setRegions(dto.regions());
        evaluatorProfile.setDivisions(dto.divisions());
        evaluatorProfile.setRvfExperience(toDiseaseExperience(dto.rvfExperience()));
        evaluatorProfile.setEvdExperience(toDiseaseExperience(dto.evdExperience()));

        return evaluatorProfileRepository.save(evaluatorProfile);
    }
}

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
}

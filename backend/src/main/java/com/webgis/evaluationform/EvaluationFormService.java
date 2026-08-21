package com.webgis.evaluationform;

import com.webgis.map.finalmap.FinalMap;
import com.webgis.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationFormService {

    private final EvaluationFormRepository evaluationFormRepository;

    public EvaluationFormService(EvaluationFormRepository evaluationFormRepository) {
        this.evaluationFormRepository = evaluationFormRepository;
    }

    /**
     * Save an evaluation form
     *
     * @param division the division which is concerned by the form
     * @param agreementLevel the agreement level of the expert for this division (1-4)
     * @param perceivedRisk the risk perceived by the expert (low-medium-hight)
     * @param certaintyLevel the certainty level of the expert on its own evaluation (1-4)
     * @param comment any comment of the expert
     * @param user the expert that produce the form
     * @param finalMap the map for which the evaluation form is made
     * @param isPublic whether the form should be public for other experts or not
     *
     * @return the newly saved evaluation form
     */
    public EvaluationForm saveForm(
            String division,
            Integer agreementLevel,
            String perceivedRisk,
            Integer certaintyLevel,
            String comment,
            User user,
            FinalMap finalMap,
            boolean isPublic){

        final EvaluationForm form = new EvaluationForm(
                division,
                agreementLevel,
                perceivedRisk,
                certaintyLevel,
                comment,
                user,
                finalMap,
                isPublic);

        return evaluationFormRepository.save(form);
    }

    /**
     * Search a form based on its id
     *
     * @param id the id of the form you are looking for
     *
     * @return the form with the specify id if it exists, empty otherwise
     */
    public Optional<EvaluationForm> findFormById(long id){ return evaluationFormRepository.findById(id);}

    /**
     * Update the value of the form with the specify id
     *
     * @param id the id of the form you want to update
     * @param division the new division for the form
     * @param agreementLevel the new agreement level for the form
     * @param perceivedRisk the new perceived risk for the form
     * @param certaintyLevel the new certainty level for the form
     * @param comment the new comment for the form
     * @param user the new user for the form
     * @param finalMap the new map to which the form is attached
     * @param isPublic whether the form should be public for other experts or not
     *
     * @throws IllegalArgumentException if there is no form with the specify id
     *
     * @return the modified evaluation form
     */
    public EvaluationForm updateForm(
            long id,
            String division,
            Integer agreementLevel,
            String perceivedRisk,
            Integer certaintyLevel,
            String comment,
            User user,
            FinalMap finalMap,
            boolean isPublic){

            final Optional<EvaluationForm> optionalForm =findFormById(id);
            if(optionalForm.isEmpty()){
                throw new IllegalArgumentException("Form does not exist");
            }

            final EvaluationForm form= optionalForm.get();

            form.setDivision(division);
            form.setAgreementLevel(agreementLevel);
            form.setPerceivedRisk(perceivedRisk);
            form.setCertaintyLevel(certaintyLevel);
            form.setComment(comment);
            form.setUser(user);
            form.setFinalMap(finalMap);
            form.setIsPublic(isPublic);

            return evaluationFormRepository.save(form);
    }

    /**
     * Get all form for a map
     *
     * @param finalMap the map from which you want the evaluation forms
     *
     * @return A list of all form linked to a specific map
     */
    public List<EvaluationForm> getAllFormForFinalMap(FinalMap finalMap){
        return evaluationFormRepository.findByFinalMap(finalMap);
    }

    /**
     * Get a form for a specific user and a specific division for a specific map
     *
     * @param user the user you are interested in
     * @param division the division you are interested in
     * @param finalMap the map from which you want the evaluation forms
     *
     * @return the form of the specify user for the specify division for a specific map if it exists, empty otherwise
     */
    public Optional<EvaluationForm> getFormForUserAndDivisionAndFinalMap(User user, String division, FinalMap finalMap){
        return evaluationFormRepository.findByUserAndDivisionAndFinalMap(user,division,finalMap);
    }

    /**
     * Check whether a user has an evaluation form for a division for a specific map
     *
     * @param user the user you want to check
     * @param division the division you want to check
     * @param finalMap the map from which you want the evaluation form
     *
     * @return True if the specify user has already a form for the specific division for a specific map , False otherwise
     */
    public boolean hasAlreadyAFormForDivisionForFinalMap(User user, String division, FinalMap finalMap){
        return evaluationFormRepository.existsByUserAndDivisionAndFinalMap(user,division,finalMap);
    }

    /**
     * Deletes the form corresponding to the id
     *
     * @param id the id of the form
     * @param user the user you want to check
     *
     * @throws IllegalArgumentException if there is no form with the specified id
     * @throws IllegalArgumentException if the user is not the owner of the form
     */
    public void deleteForm(long id, User user){
        final Optional<EvaluationForm> optionalForm = findFormById(id);
        if(optionalForm.isEmpty()){
            throw new IllegalArgumentException("Form does not exist");
        }
        final EvaluationForm form = optionalForm.get();
        if (!form.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to delete this form");
        }
        evaluationFormRepository.delete(form);
    }

    /**
     * Get the number of public evaluation forms for a map
     *
     * @param finalMap the map you are interested in
     *
     * @return the number of public evaluation forms for the map given in parameter
     */
    public Long getNumberOfPublicFormForAMap(FinalMap finalMap) {
        return evaluationFormRepository.countByFinalMapAndIsPublicTrue(finalMap);
    }

    /**
     * Get the number of public evaluation forms for a specific division of a map
     *
     * @param division the division you are interested in
     * @param finalMap the map you are interested in
     *
     * @return the number of public evaluation forms for a specific division of a map
     */
    public Long getNumberOfPublicFormForADivisionOfAMap(String division, FinalMap finalMap) {
        return evaluationFormRepository.countByFinalMapAndDivisionAndIsPublicTrue(finalMap, division);
    }

    /**
     * Get all evaluation forms submitted by a specific user for a specific map
     *
     * @param finalMap the map you are interested in
     * @param user the user you are interested in
     *
     * @return a list of all evaluation forms the user submitted for that map (one per division at most)
     */
    public List<EvaluationForm> getAllFormForFinalMapAndUser(FinalMap finalMap, User user){
        return evaluationFormRepository.findByFinalMapAndUser(finalMap, user);
    }

}

package com.webgis.evaluationform;

import com.webgis.MessageDto;
import com.webgis.evaluatorprofile.EvaluatorProfileService;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.security.CookieService;
import com.webgis.security.JwtService;
import com.webgis.user.User;
import com.webgis.user.UserService;
import com.webgis.evaluationform.dto.ResponseEvaluationFormDto;
import com.webgis.evaluationform.dto.SaveEvaluationFormDto;
import com.webgis.evaluationform.dto.UpdateEvaluationFormDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/evaluationForm")
public class EvaluationFormController {

    private final EvaluationFormService evaluationFormService;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final UserService userService;
    private final FinalMapService finalMapService;
    private final EvaluatorProfileService evaluatorProfileService;

    public EvaluationFormController(EvaluationFormService evaluationFormService,
                                    JwtService jwtService,
                                    CookieService cookieService,
                                    UserService userService,
                                    FinalMapService finalMapService,
                                    EvaluatorProfileService evaluatorProfileService) {
        this.evaluationFormService = evaluationFormService;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.userService = userService;
        this.finalMapService=finalMapService;
        this.evaluatorProfileService = evaluatorProfileService;
    }

    /**
     * Save a new form
     *
     * @param saveFormDto information needed to save a new form
     * @param request the Http request containing the JWT token
     *
     * @return saved form information if it succeeds, error message otherwise
     */
    @PostMapping("/saveForm")
    public ResponseEntity<Object> saveForm(
            @RequestBody @Valid SaveEvaluationFormDto saveFormDto,
            HttpServletRequest request){

        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);
        final Optional<FinalMap> optionalFinalMap= finalMapService.findById(saveFormDto.finalMapId());

        if(optionalFinalMap.isEmpty()){
            return ResponseEntity.status(404).body(new MessageDto("The selected map does not exist"));
        }

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user= optionalUser.get();

        if (!evaluatorProfileService.hasProfile(user)) {
            return ResponseEntity.status(403)
                    .body(new MessageDto("You must complete your evaluator profile before submitting evaluations"));
        }

        final FinalMap finalMap= optionalFinalMap.get();

        if(evaluationFormService.hasAlreadyAFormForDivisionForFinalMap(user, saveFormDto.division(),finalMap)){
            return ResponseEntity.status(401).body(new MessageDto("You have already a evaluation form for this division in this map"));
        }

        try{
            final EvaluationForm evaluationForm = evaluationFormService.saveForm(
                    saveFormDto.division(),
                    saveFormDto.agreementLevel(),
                    saveFormDto.perceivedRisk(),
                    saveFormDto.certaintyLevel(),
                    saveFormDto.comment(),
                    user,
                    finalMap,
                    saveFormDto.isPublic());
            final ResponseEvaluationFormDto responseEvaluationFormDto = new ResponseEvaluationFormDto(evaluationForm);
            return ResponseEntity.status(200).body(responseEvaluationFormDto);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageDto(e.getMessage()));
        }


    }

    /**
     * Update a form
     *
     * @param updateEvaluationFormDto  information of about the form you want to update
     * @param request the Http request containing the JWT token
     *
     * @return updated form information if it succeeds, error message otherwise
     */
    @PutMapping("/updateForm")
    public ResponseEntity<Object> updateForm(
            @RequestBody @Valid UpdateEvaluationFormDto updateEvaluationFormDto,
            HttpServletRequest request){

        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final Optional<EvaluationForm> optionalEvaluationForm = evaluationFormService.findFormById(updateEvaluationFormDto.id());

        if(optionalEvaluationForm.isEmpty()){
            return ResponseEntity.status(404).body(new MessageDto("No form with the specify id"));
        }

        final User user= optionalUser.get();
        final EvaluationForm evaluationForm =optionalEvaluationForm.get();

        if(!user.equals(evaluationForm.getUser()) && !user.isAdmin()){
            return ResponseEntity.status(401).body(new MessageDto("You are not an admin or the author of this form"));
        }

        try {

            final EvaluationForm newEvaluationForm = evaluationFormService.updateForm(
                    updateEvaluationFormDto.id(),
                    evaluationForm.getDivision(),
                    updateEvaluationFormDto.agreementLevel(),
                    updateEvaluationFormDto.perceivedRisk(),
                    updateEvaluationFormDto.certaintyLevel(),
                    updateEvaluationFormDto.comment(),
                    user,
                    evaluationForm.getFinalMap(),
                    updateEvaluationFormDto.isPublic());
            final ResponseEvaluationFormDto responseEvaluationFormDto = new ResponseEvaluationFormDto(newEvaluationForm);
            return ResponseEntity.status(200).body(responseEvaluationFormDto);

        }catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageDto(e.getMessage()));
        }
    }


    /**
     * Get the form from the connected user for a specific division for a specific map
     *
     * @param finalMapId the id of the map you are intrested in
     * @param division the division you are interested in
     * @param request the Http request containing the JWT token
     *
     * @return the form information if it exists, error message or not found otherwise
     */
    @GetMapping("/myForm/{finalMapId}/{division}")
    public ResponseEntity<Object> getConnectUserFormForFinalMapForDivision(
            @PathVariable long finalMapId,
            @PathVariable String division,
            HttpServletRequest request
            ){

        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);
        final Optional<FinalMap> optionalFinalMap= finalMapService.findById(finalMapId);

        if(optionalFinalMap.isEmpty()){
            return ResponseEntity.status(404).body(new MessageDto("The selected map does not exist"));
        }

        if(optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user= optionalUser.get();
        final FinalMap finalMap= optionalFinalMap.get();

        final Optional<EvaluationForm> optionalEvaluationForm = evaluationFormService
                .getFormForUserAndDivisionAndFinalMap(
                user,
                division,
                finalMap);

        if(optionalEvaluationForm.isEmpty()){
            return ResponseEntity.status(404).body(new MessageDto("You have no form for the specified division for this map"));
        }

        final EvaluationForm evaluationForm = optionalEvaluationForm.get();
        final ResponseEvaluationFormDto responseEvaluationFormDto = new ResponseEvaluationFormDto(evaluationForm);
        return ResponseEntity.status(200).body(responseEvaluationFormDto);
    }

    /**
     * Get all existing form for a specific map
     *
     * @param finalMapId the id of the map you are interested in
     *
     * @return the froms information for a map in a list, not found if the map doesn't exist
     */
    @GetMapping("/allFormForFinalMap/{finalMapId}")
    public ResponseEntity<Object> getAllFormForFinalMap(
            @PathVariable long finalMapId
    ){
        final Optional<FinalMap> optionalFinalMap= finalMapService.findById(finalMapId);
        if(optionalFinalMap.isEmpty()){
            return ResponseEntity.status(404).body(new MessageDto("The selected map does not exist"));
        }
        final FinalMap finalMap= optionalFinalMap.get();

        final List<EvaluationForm> evaluationForms = evaluationFormService.getAllFormForFinalMap(finalMap);
        final List<ResponseEvaluationFormDto> responseEvaluationForms = new ArrayList<>();
        for(EvaluationForm evaluationForm : evaluationForms){
            if (evaluationForm.getUser().isDeleted()) {
                evaluationForm.getUser().setUsername("Deleted user");
                evaluationForm.getUser().setFirstName(" ");
                evaluationForm.getUser().setLastName(" ");
            }
            responseEvaluationForms.add(new ResponseEvaluationFormDto(evaluationForm));
        }
        return ResponseEntity.status(200).body(responseEvaluationForms);
    }

    /**
     * Delete a form by id if the requesting user is the owner
     *
     * @param id the id of the form to delete
     * @param request the Http request containing the JWT token
     *
     * @return the deleted form confirmation if it succeeds, error message otherwise
     */
    @DeleteMapping("/deleteForm/{id}")
    public ResponseEntity<Object> deleteForm(
            @PathVariable long id,
            HttpServletRequest request
    ) {
        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("Incorrect credentials"));
        }

        final User user = optionalUser.get();

        try {
            evaluationFormService.deleteForm(id, user);
            return ResponseEntity.status(200).body(new MessageDto("Form has been deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageDto(e.getMessage()));
        }
    }
}

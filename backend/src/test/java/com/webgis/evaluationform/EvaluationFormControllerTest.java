package com.webgis.evaluationform;

import com.webgis.evaluationform.dto.ResponseEvaluationFormDto;
import com.webgis.evaluationform.dto.SaveEvaluationFormDto;
import com.webgis.evaluationform.dto.UpdateEvaluationFormDto;
import com.webgis.evaluatorprofile.EvaluatorProfileService;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.security.CookieService;
import com.webgis.security.JwtService;
import com.webgis.user.User;
import com.webgis.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationFormControllerTest {

    @Mock
    private EvaluationFormService evaluationFormService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CookieService cookieService;

    @Mock
    private UserService userService;

    @Mock
    private FinalMapService finalMapService;

    @InjectMocks
    private EvaluationFormController controller;

    @Mock
    private EvaluatorProfileService evaluatorProfileService;

    private User user;
    private FinalMap finalMap;
    private EvaluationForm evaluationForm;
    private HttpServletRequest request;

    @BeforeEach
    void SetUp() {
        user = new User(
                "pseudo",
                "Julien",
                "Jamal",
                "julien.jamal@outlook.com",
                "password",
                "Admin");
        user.setId(1L);

        byte[] dataZip = {66};
        List<String> list = new ArrayList<>(List.of("dry"));
        finalMap = new FinalMap("title",
                "risk map",
                list,
                dataZip,
                "file");
        finalMap.setId(1L);

        evaluationForm = new EvaluationForm(
                "Djerem",
                4,
                "High",
                4,
                "comment",
                user,
                finalMap,
                true
        );
        evaluationForm.setId(1L);

        request = mock(HttpServletRequest.class);
    }

    // saveForm Test
    @Test
    void saveFormShouldReturnOkAndAEvaluationFormTest() {

        //Arrange
        SaveEvaluationFormDto dto = new SaveEvaluationFormDto(
                "Djerem",
                2,
                "High",
                2,
                "comment",
                1L,
                true);
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));
        when(evaluatorProfileService.hasProfile(user)).thenReturn(true);
        when(finalMapService.findById(1L)).thenReturn(Optional.of(finalMap));
        when(evaluationFormService.hasAlreadyAFormForDivisionForFinalMap(user, "Djerem", finalMap)).thenReturn(false);
        when(evaluationFormService.saveForm(any(), any(), any(), any(), any(), eq(user), eq(finalMap), anyBoolean()))
                .thenReturn(evaluationForm);

        //Act
        ResponseEntity<Object> response = controller.saveForm(dto, request);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isInstanceOf(ResponseEvaluationFormDto.class);
    }

    @Test
    void saveFormAlreadyAnEvalShouldReturnUnauthorizeTest() {
        //Arrange
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));
        when(evaluatorProfileService.hasProfile(user)).thenReturn(true);

        SaveEvaluationFormDto dto = new SaveEvaluationFormDto(
                "Djerem",
                2,
                "High",
                2,
                "comment",
                1L,
                true);

        when(finalMapService.findById(1L)).thenReturn(Optional.of(finalMap));
        when(evaluationFormService.hasAlreadyAFormForDivisionForFinalMap(user, "Djerem", finalMap)).thenReturn(true);

        //Act
        ResponseEntity<Object> response = controller.saveForm(dto, request);

        //Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    //updateForm Test

    @Test
    void updateFormShouldReturnOkandFormWiththeNewDataTest() {
        //Arrange
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));

        UpdateEvaluationFormDto dto = new UpdateEvaluationFormDto(
                1L,
                2,
                "low",
                4,
                "new comment",
                false
        );

        //will play the role of the new evaluation form
        EvaluationForm updatedEvaluationForm = new EvaluationForm(
                evaluationForm.getDivision(),
                2,
                "low",
                4,
                "new comment",
                user,
                finalMap,
                false
        );
        updatedEvaluationForm.setId(1L);

        when(evaluationFormService.findFormById(1L)).thenReturn(Optional.of(evaluationForm));
        when(evaluationFormService.updateForm(
                dto.id(),
                "Djerem",
                dto.agreementLevel(),
                dto.perceivedRisk(),
                dto.certaintyLevel(),
                dto.comment(),
                user,
                finalMap,
                dto.isPublic()))
                .thenReturn(updatedEvaluationForm);

        //Act
        ResponseEntity<Object> response = controller.updateForm(dto, request);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isInstanceOf(ResponseEvaluationFormDto.class);
        verify(evaluationFormService).updateForm(
                dto.id(),
                "Djerem",
                dto.agreementLevel(),
                dto.perceivedRisk(),
                dto.certaintyLevel(),
                dto.comment(),
                user, finalMap,
                dto.isPublic());
    }

    //getConnectUserFormForFinalMapForDivision Test

    @Test
    void getConnectUserFormForFinalMapForDivisionShouldReturnOkAndTheformTest() {
        //Arrange
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));

        when(finalMapService.findById(1L)).thenReturn(Optional.of(finalMap));
        when(evaluationFormService
                .getFormForUserAndDivisionAndFinalMap(user, "Djerem", finalMap))
                .thenReturn(Optional.of(evaluationForm));

        //Act
        ResponseEntity<Object> response = controller.getConnectUserFormForFinalMapForDivision(1L, "Djerem", request);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isInstanceOf(ResponseEvaluationFormDto.class);
    }

    @Test
    void getConnectUserFormForFinalMapForDivisionShouldReturnNotFoundNoEvaluationTest() {
        //Arrange
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));

        when(finalMapService.findById(1L)).thenReturn(Optional.of(finalMap));
        when(evaluationFormService
                .getFormForUserAndDivisionAndFinalMap(user, "Haut Nyong", finalMap))
                .thenReturn(Optional.empty());

        //Act
        ResponseEntity<Object> response = controller.getConnectUserFormForFinalMapForDivision(1L, "Haut Nyong", request);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // getAllFormForFinalMap Test
    @Test
    void getAllFormForFinalMapShouldReturnFormsAndAnonymizeDeletedUsersTest() {
        // Arrange
        User deletedUser = new User(
                "deletedpseudo",
                "Julien",
                "Jamal",
                "julien.jamal@outlook.com",
                "password",
                "USER");
        deletedUser.setId(2L);
        deletedUser.setDeleted(true);

        EvaluationForm formDeletedUser = new EvaluationForm(
                "Djerem",
                3,
                "medium",
                2,
                "comment",
                deletedUser,
                finalMap,
                true
        );

        when(finalMapService.findById(1L)).thenReturn(Optional.of(finalMap));
        when(evaluationFormService.getAllFormForFinalMap(finalMap))
                .thenReturn(List.of(evaluationForm, formDeletedUser));

        // Act
        ResponseEntity<Object> response = controller.getAllFormForFinalMap(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isInstanceOf(List.class);

        List<ResponseEvaluationFormDto> body = (List<ResponseEvaluationFormDto>) response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        assertEquals("Deleted user", body.get(1).username());
    }

   // deleteForm Test
    @Test
    void deleteFormShouldReturnOkWhenFormIsDeletedSuccessfullyTest() {
        // Arrange
        when(cookieService.getJwtFromCookie(request)).thenReturn("token");
        when(jwtService.extractUsername("token")).thenReturn("pseudo");
        when(userService.findByUsername("pseudo")).thenReturn(Optional.of(user));

        doNothing().when(evaluationFormService).deleteForm(1L, user);

        // Act
        ResponseEntity<Object> response = controller.deleteForm(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(evaluationFormService).deleteForm(1L, user);
    }

}



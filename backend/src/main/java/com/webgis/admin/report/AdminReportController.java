package com.webgis.admin.user;

import com.webgis.MessageDto;
import com.webgis.admin.dto.user.UserSummaryDto;
import com.webgis.admin.report.ExcelReportService;
import com.webgis.evaluationform.EvaluationForm;
import com.webgis.evaluationform.EvaluationFormService;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.user.User;
import com.webgis.user.UserService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/admin")
public class AdminReportController {

    private final UserService userService;
    private final FinalMapService finalMapService;
    private final EvaluationFormService evaluationFormService;
    private final ExcelReportService excelReportService;



    public AdminReportController(
            UserService userService,
            FinalMapService finalMapService,
            EvaluationFormService evaluationFormService,
            ExcelReportService excelReportService){
        this.userService = userService;
        this.finalMapService = finalMapService;
        this.evaluationFormService = evaluationFormService;
        this.excelReportService = excelReportService;
    }

    /**
     * returns all the users info
     */
    @GetMapping("/getUserReport/{mapId}/{userId}")
    public ResponseEntity<Object> getUserReport(
            @PathVariable long mapId,
            @PathVariable long userId
    ) {
        final Optional<FinalMap> optionalFinalMap = finalMapService.findById(mapId);
        if (optionalFinalMap.isEmpty()) {
            return ResponseEntity.status(404).body(new MessageDto("The selected map does not exist"));
        }

        final Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new MessageDto("The selected user does not exist"));
        }

        final FinalMap finalMap = optionalFinalMap.get();
        final User user = optionalUser.get();

        final List<EvaluationForm> evaluationForms = evaluationFormService.getAllFormForFinalMapAndUser(finalMap, user);

        try {
            final byte[] excelBytes = excelReportService.generateUserReport(evaluationForms);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=user-report.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new MessageDto("Failed to generate report"));
        }
    }

    @GetMapping("/getEvaluators/{mapId}")
    public ResponseEntity<Object> getEvaluators(@PathVariable long mapId) {

        final Optional<FinalMap> optionalFinalMap = finalMapService.findById(mapId);
        if (optionalFinalMap.isEmpty()) {
            return ResponseEntity.status(404).body(new MessageDto("The selected map does not exist"));
        }

        final FinalMap finalMap = optionalFinalMap.get();
        final List<EvaluationForm> evaluationForms = evaluationFormService.getAllFormForFinalMap(finalMap);

        final List<UserSummaryDto> evaluators = evaluationForms.stream()
                .map(EvaluationForm::getUser)
                .distinct()
                .map(u -> new UserSummaryDto(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail()))
                .toList();

        return ResponseEntity.ok(evaluators);
    }
}
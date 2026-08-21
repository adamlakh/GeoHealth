package com.webgis.admin.report;

import com.webgis.evaluationform.EvaluationForm;
import com.webgis.evaluatorprofile.DiseaseExperience;
import com.webgis.evaluatorprofile.EvaluatorProfile;
import com.webgis.evaluatorprofile.EvaluatorProfileService;
import com.webgis.user.User;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelReportService {
    EvaluatorProfileService evaluatorProfileService;

    public ExcelReportService(EvaluatorProfileService evaluatorProfileService) {
        this.evaluatorProfileService = evaluatorProfileService;
    }

    /**
     * Builds an .xlsx report of evaluation forms, one row per form. Columns are
     * generated from EvaluationForm's fields (excluding "user" and "finalMap"),
     * using reflection so the report stays in sync if fields are added or removed.
     *
     * @param evaluationForms the forms to include, one per row
     * @return the .xlsx report as a byte array
     * @throws IOException if the workbook cannot be written
     */
    public byte[] generateUserReport(List<EvaluationForm> evaluationForms) throws IOException {

        List<Field> fields = Arrays.stream(EvaluationForm.class.getDeclaredFields())
                .filter(f -> !f.getName().equals("user") && !f.getName().equals("finalMap"))
                .toList();
        fields.forEach(f -> f.setAccessible(true));

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Sheet sheet = workbook.createSheet("Report");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++) {
                headerRow.createCell(i).setCellValue(fields.get(i).getName());
            }

            int rowIndex = 1;
            for (EvaluationForm form : evaluationForms) {
                Row row = sheet.createRow(rowIndex);
                rowIndex++;

                for (int i = 0; i < fields.size(); i++) {
                    try {
                        Object value = fields.get(i).get(form);
                        if (value != null) {
                            row.createCell(i).setCellValue(value.toString());
                        }
                    } catch (IllegalAccessException e) {
                        // skip
                    }
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Builds an .xlsx export of a user's identity info and evaluator profile
     * (no evaluation data). Sheet is named "Profile": user info in rows 0-1,
     * then, if the user has a profile, a labeled section with their profile
     * fields and disease experience scores below. If they have no profile,
     * the sheet just ends after row 1.
     *
     * @param user the user to export
     * @return the .xlsx file as a byte array
     * @throws IOException if the workbook cannot be written
     */
    public byte[] generateUserProfile(User user) throws IOException {

        final Optional<EvaluatorProfile> profile = evaluatorProfileService.getProfileForUser(user);

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            final Sheet sheet = workbook.createSheet("Profile");

            final Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Username");
            headerRow.createCell(1).setCellValue("First Name");
            headerRow.createCell(2).setCellValue("Last Name");
            headerRow.createCell(3).setCellValue("email");

            final Row valueRow = sheet.createRow(1);
            valueRow.createCell(0).setCellValue(user.getUsername());
            valueRow.createCell(1).setCellValue(user.getFirstName());
            valueRow.createCell(2).setCellValue(user.getLastName());
            valueRow.createCell(3).setCellValue(user.getEmail());

            if (profile.isPresent()) {
                int rowIndex = 5;
                final EvaluatorProfile evaluatorProfile = profile.get();

                final Row profileTitleRow = sheet.createRow(rowIndex++);
                profileTitleRow.createCell(0).setCellValue("Evaluator Profile");

                rowIndex = writeLabelValueRow(sheet, rowIndex, "Professions", joinList(evaluatorProfile.getProfessions()));
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Sectors", joinList(evaluatorProfile.getSectors()));
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Intervention Levels", joinList(evaluatorProfile.getInterventionLevels()));
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Sectors Worked In", joinList(evaluatorProfile.getSectorsWorkedIn()));
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Countries", evaluatorProfile.getCountries());
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Regions", evaluatorProfile.getRegions());
                rowIndex = writeLabelValueRow(sheet, rowIndex, "Divisions", evaluatorProfile.getDivisions());

                rowIndex++;
                rowIndex = writeDiseaseExperienceSection(sheet, rowIndex, "RVF Experience", evaluatorProfile.getRvfExperience());

                rowIndex++;
                writeDiseaseExperienceSection(sheet, rowIndex, "EVD Experience", evaluatorProfile.getEvdExperience());
            }

            workbook.write(out);
            return out.toByteArray();
        }

    }

    /**
     * Writes one disease experience section (title + its 7 score/answer fields)
     * as label/value rows, starting at rowIndex.
     *
     * @param sheet the sheet to write to
     * @param rowIndex the row to start writing at
     * @param title the section title (e.g. "RVF Experience")
     * @param experience the disease experience data to write
     * @return the next free row index after this section
     */
    private int writeDiseaseExperienceSection(Sheet sheet, int rowIndex, String title, DiseaseExperience experience) {
        final Row titleRow = sheet.createRow(rowIndex++);
        titleRow.createCell(0).setCellValue(title);

        rowIndex = writeLabelValueRow(sheet, rowIndex, "Pathogen Knowledge Score", experience.getPathogenKnowledgeScore());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Transmission Knowledge Score", experience.getTransmissionKnowledgeScore());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Animal Clinical Knowledge Score", experience.getAnimalClinicalKnowledgeScore());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Human Clinical Knowledge Score", experience.getHumanClinicalKnowledgeScore());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Professionally Exposed", experience.isProfessionallyExposed());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Exposure Frequency", experience.getExposureFrequency());
        rowIndex = writeLabelValueRow(sheet, rowIndex, "Years Involved", experience.getYearsInvolved());

        return rowIndex;
    }

    /**
     * Writes one label/value pair as a row: label in column 0, value in column 1.
     *
     * @param sheet the sheet to write to
     * @param rowIndex the row to write
     * @param label the text for column 0
     * @param value the value for column 1 (null becomes an empty cell)
     * @return the next free row index
     */
    private int writeLabelValueRow(Sheet sheet, int rowIndex, String label, Object value) {
        final Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value == null ? "" : value.toString());
        return rowIndex + 1;
    }

    /**
     * Joins a list of strings into one comma-separated string, or "" if null.
     *
     * @param values the list to join
     * @return the joined string
     */
    private String joinList(List<String> values) {
        return values == null ? "" : String.join(", ", values);
    }
}

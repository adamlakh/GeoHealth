package com.webgis.admin.report;

import com.webgis.evaluationform.EvaluationForm;
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

@Service
public class ExcelReportService {

    public byte[] generateUserReport(List<EvaluationForm> evaluationForms) throws IOException {

        List<Field> fields = Arrays.stream(EvaluationForm.class.getDeclaredFields())
                .filter(f -> !f.getName().equals("user") && !f.getName().equals("finalMap"))
                .toList();
        fields.forEach(f -> f.setAccessible(true));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
}

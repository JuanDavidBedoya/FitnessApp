package com.example.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public static void generateReport(String reportName, List<String> columnNames, List<Map<String, String>> data) throws Exception {
        String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
        Path downloadsDir = Paths.get(downloadsPath);
        Files.createDirectories(downloadsDir);
        String safeReportName = reportName.replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = downloadsPath + safeReportName + "_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("REPORTE GENERADO: " + reportName.toUpperCase()));
        document.add(new Paragraph("Fecha y Hora de Generación: " + new java.util.Date()));
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(columnNames.size())).useAllAvailableWidth();

        for (String columnName : columnNames) {
            table.addHeaderCell(new Cell().add(new Paragraph(columnName)));
        }

        for (Map<String, String> row : data) {
            for (String columnName : columnNames) {
                String value = row.get(columnName) == null ? "" : row.get(columnName);
                table.addCell(new Cell().add(new Paragraph(value)));
            }
        }

        document.add(table);
        document.close();

        Alerts.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
                         "Descarga Exitosa",
                         "El reporte '" + reportName + "' ha sido generado y descargado como PDF en la carpeta Descargas. Archivo: " + new java.io.File(fileName).getName());
    }
}
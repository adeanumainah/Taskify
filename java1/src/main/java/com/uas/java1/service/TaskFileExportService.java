package com.uas.java1.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.uas.java1.model.Task;
import com.uas.java1.model.TaskFile;
import com.uas.java1.repository.TaskFileRepository;
import com.uas.java1.repository.TaskRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskFileExportService {

    private final TaskFileRepository taskFileRepository;
    private final TaskRepository taskRepository;

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<TaskFile> files = taskFileRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Task Files");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Task ID");
            header.createCell(2).setCellValue("File Name");
            header.createCell(3).setCellValue("Upload Time");

            int rowIdx = 1;
            for (TaskFile tf : files) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tf.getId());
                row.createCell(1).setCellValue(tf.getTask().getId());
                row.createCell(2).setCellValue(tf.getFileName());
                row.createCell(3).setCellValue(tf.getUploadedAt().toString());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public void exportToExcellLaporan(int tahun, int bulan, HttpServletResponse response)
            throws IOException {
        List<Task> daftarTugas = taskRepository.findAllByDeadlineYearAndMonth(tahun, bulan);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan_Tugas_" + tahun + "_" + bulan);
            CreationHelper creationHelper = workbook.getCreationHelper();

            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(borderStyle);
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.cloneStyleFrom(borderStyle);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle styleUntukPending = workbook.createCellStyle();
            styleUntukPending.cloneStyleFrom(borderStyle);
            styleUntukPending.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            styleUntukPending.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle styleUntukInProgress = workbook.createCellStyle();
            styleUntukInProgress.cloneStyleFrom(borderStyle);
            styleUntukInProgress.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            styleUntukInProgress.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle styleUntukCompleted = workbook.createCellStyle();
            styleUntukCompleted.cloneStyleFrom(borderStyle);
            styleUntukCompleted.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            styleUntukCompleted.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle styleUntukDeadlineLewat = workbook.createCellStyle();
            styleUntukDeadlineLewat.cloneStyleFrom(borderStyle);
            styleUntukDeadlineLewat.setFillForegroundColor(IndexedColors.RED.getIndex());
            styleUntukDeadlineLewat.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleUntukDeadlineLewat.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));


            Row header = sheet.createRow(0);
            String[] columHeader = { "Nama Karyawan", "Judul Tugas", "Deskripsi", "Dealine", "Status",
                    "Tanggal Selesai" };
            for (int i = 0; i < columHeader.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columHeader[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Task tugas : daftarTugas) {
                Row row = sheet.createRow(rowIndex++);

                Cell cellNamaKaryawan = row.createCell(0);
                cellNamaKaryawan.setCellValue(tugas.getAssignedTo().getUsername());
                cellNamaKaryawan.setCellStyle(borderStyle);

                Cell cellJudulTugas = row.createCell(1);
                cellJudulTugas.setCellValue(tugas.getTitle());

                Cell cellDeskripsi = row.createCell(2);
                cellDeskripsi.setCellValue(tugas.getDescription());

                Cell cellDeadline = row.createCell(3);
                cellDeadline.setCellValue(Date.valueOf(tugas.getDeadline()));
                if (tugas.getDeadline().isBefore(LocalDate.now()) && !tugas.getStatus().equalsIgnoreCase("COMPLETED")) {
                    cellDeadline.setCellStyle(styleUntukDeadlineLewat);
                } else {
                    cellDeadline.setCellStyle(dateStyle);
                }

                Cell cellStatus = row.createCell(4);
                cellStatus.setCellValue(tugas.getStatus());
                switch (tugas.getStatus().toUpperCase()) {
                    case "PENDING":
                        cellStatus.setCellStyle(styleUntukPending);
                        break;
                    case "IN_PROGRESS":
                        cellStatus.setCellStyle(styleUntukInProgress);
                        break;
                    case "COMPLETED":
                        cellStatus.setCellStyle(styleUntukCompleted);
                        break;

                    default:
                        cellStatus.setCellStyle(borderStyle);
                        break;
                }

                Optional<TaskFile> file = taskFileRepository.findFirstByTaskOrderByUploadedAtDesc(tugas);
                if (tugas.getStatus().equalsIgnoreCase("COMPLETED") && file.isPresent()) {
                    row.createCell(5).setCellValue(file.get().getUploadedAt().toString());
                } else {
                    row.createCell(5).setCellValue("-");
                }

            }
            for (int i = 0; i < columHeader.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=laporan_tugas_" + tahun + "_" + bulan + ".xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();
        }
    }
}

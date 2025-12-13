package com.class_manager.user_auth_service.service;

import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;
import com.class_manager.user_auth_service.model.entity.Student;
import com.class_manager.user_auth_service.model.entity.Teacher;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    public List<StudentDto> parseStudentExcel(MultipartFile file) throws IOException {
        List<StudentDto> students = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                StudentDto s = StudentDto.builder()
                        .firstname(formatter.formatCellValue(row.getCell(0)))
                        .lastname(formatter.formatCellValue(row.getCell(1)))
                        .email(formatter.formatCellValue(row.getCell(2)))
                        .apogeeNumber(formatter.formatCellValue(row.getCell(3)))
                        .build();


                students.add(s);
            }
        }

        return students;
    }
    public List<TeacherDto> parseTeacherExcel(MultipartFile file) throws IOException {
        List<TeacherDto> teachers= new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                TeacherDto t = TeacherDto.builder()
                        .firstname(formatter.formatCellValue(row.getCell(0)))
                        .lastname(formatter.formatCellValue(row.getCell(1)))
                        .email(formatter.formatCellValue(row.getCell(2)))
                        .teacherCode(formatter.formatCellValue(row.getCell(3)))
                        .speciality(formatter.formatCellValue(row.getCell(4)))
                        .build();
                teachers.add(t);
            }
        }

        return teachers;
    }
}


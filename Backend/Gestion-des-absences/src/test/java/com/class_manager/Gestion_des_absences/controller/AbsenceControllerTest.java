package com.class_manager.Gestion_des_absences.controller;

import com.class_manager.Gestion_des_absences.model.dto.AbsenceResponseDTO;
import com.class_manager.Gestion_des_absences.model.dto.SeanceDTO;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.service.AbsenceService;
import com.class_manager.Gestion_des_absences.service.SeanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbsenceControllerTest {

    @Mock
    private SeanceService seanceService;

    @Mock
    private AbsenceService absenceService;

    @InjectMocks
    private AbsenceController absenceController;

    private Seance testSeance;
    private AbsenceResponseDTO testAbsenceResponse;

    @BeforeEach
    void setUp() {
        testSeance = new Seance();
        testSeance.setId(1L);
        testSeance.setProfId(10L);
        testSeance.setClasseId(1L);
        testSeance.setCreneauId(1L);
        testSeance.setDate(LocalDate.now());
        testSeance.setMatiereId(1L);
        testSeance.setSalleId(1L);

        testAbsenceResponse = new AbsenceResponseDTO();
        testAbsenceResponse.setId(1L);
        testAbsenceResponse.setEtudiantId(100L);
        testAbsenceResponse.setPresent(false);
        testAbsenceResponse.setJustifie(false);
    }

    @Test
    void testGetSeancesForUser_Success() {
      
        Long classeId = 1L;
        Long userId = 10L;
        List<Seance> seances = Arrays.asList(testSeance);

        when(seanceService.getSeancesByClasseAndUser(classeId, userId)).thenReturn(seances);

      
        ResponseEntity<List<Seance>> response = absenceController.getSeancesForUser(classeId, userId);

     
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testSeance.getId(), response.getBody().get(0).getId());
        verify(seanceService, times(1)).getSeancesByClasseAndUser(classeId, userId);
    }

    @Test
    void testGetAbsencesByStudent_Success() {
     
        Long etudiantId = 100L;
        List<AbsenceResponseDTO> absences = Arrays.asList(testAbsenceResponse);

        when(absenceService.getAbsencesByEtudiantId(etudiantId)).thenReturn(absences);

    
        ResponseEntity<List<AbsenceResponseDTO>> response = absenceController.getAbsencesByStudent(etudiantId);

    
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testAbsenceResponse.getId(), response.getBody().get(0).getId());
        verify(absenceService, times(1)).getAbsencesByEtudiantId(etudiantId);
    }

    @Test
    void testUploadJustification_Success() throws Exception {
      
        Long absenceId = 1L;
        org.springframework.web.multipart.MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceService.uploadJustification(eq(absenceId), any())).thenReturn(testAbsenceResponse);

      
        ResponseEntity<AbsenceResponseDTO> response = absenceController.uploadJustification(absenceId, file);

     
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAbsenceResponse.getId(), response.getBody().getId());
        verify(absenceService, times(1)).uploadJustification(eq(absenceId), any());
    }

    @Test
    void testUploadJustification_IOException() throws Exception {
       
        Long absenceId = 1L;
        org.springframework.web.multipart.MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceService.uploadJustification(eq(absenceId), any()))
                .thenThrow(new java.io.IOException("File error"));

      
        ResponseEntity<AbsenceResponseDTO> response = absenceController.uploadJustification(absenceId, file);

 
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(absenceService, times(1)).uploadJustification(eq(absenceId), any());
    }

    @Test
    void testUploadJustification_AbsenceNotFound() throws Exception {
    
        Long absenceId = 999L;
        org.springframework.web.multipart.MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceService.uploadJustification(eq(absenceId), any()))
                .thenThrow(new RuntimeException("Absence non trouvée"));

      
        ResponseEntity<AbsenceResponseDTO> response = absenceController.uploadJustification(absenceId, file);

   
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(absenceService, times(1)).uploadJustification(eq(absenceId), any());
    }

    @Test
    void testSaveAbsences_Success() {
     
        SeanceDTO request = new SeanceDTO();
        request.setProfId(10L);
        request.setClasseId(1L);
        request.setCreneauId(1L);
        request.setDate("2024-01-15");
        request.setMatiereId(1L);
        request.setSalleId(1L);

        when(seanceService.saveSeanceWithAbsences(any(SeanceDTO.class))).thenReturn(testSeance);

      
        ResponseEntity<Seance> response = absenceController.saveAbsences(request);

    
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testSeance.getId(), response.getBody().getId());
        verify(seanceService, times(1)).saveSeanceWithAbsences(any(SeanceDTO.class));
    }

    @Test
    void testUpdateJustificationStatus_Accept() {
       
        Long absenceId = 1L;
        boolean justifie = true;
        testAbsenceResponse.setJustifie(true);

        when(absenceService.updateJustificationStatus(absenceId, justifie)).thenReturn(testAbsenceResponse);

    
        ResponseEntity<AbsenceResponseDTO> response = absenceController.updateJustificationStatus(absenceId, justifie);

      
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isJustifie());
        verify(absenceService, times(1)).updateJustificationStatus(absenceId, justifie);
    }

    @Test
    void testUpdateJustificationStatus_Reject() {
      
        Long absenceId = 1L;
        boolean justifie = false;
        testAbsenceResponse.setJustifie(false);

        when(absenceService.updateJustificationStatus(absenceId, justifie)).thenReturn(testAbsenceResponse);

     
        ResponseEntity<AbsenceResponseDTO> response = absenceController.updateJustificationStatus(absenceId, justifie);

   
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isJustifie());
        verify(absenceService, times(1)).updateJustificationStatus(absenceId, justifie);
    }

    @Test
    void testUpdateJustificationStatus_AbsenceNotFound() {
      
        Long absenceId = 999L;
        boolean justifie = true;

        when(absenceService.updateJustificationStatus(absenceId, justifie))
                .thenThrow(new RuntimeException("Absence non trouvée"));

        // When
        ResponseEntity<AbsenceResponseDTO> response = absenceController.updateJustificationStatus(absenceId, justifie);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(absenceService, times(1)).updateJustificationStatus(absenceId, justifie);
    }

    @Test
    void testGetJustificationFile_Success() throws Exception {
        // Given
        String filename = "test_file.pdf";
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test_justification_", ".pdf");
        java.nio.file.Files.write(tempFile, "test pdf content".getBytes());
        
        // Move file to the expected location
        java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads/justifications/");
        if (!java.nio.file.Files.exists(uploadDir)) {
            java.nio.file.Files.createDirectories(uploadDir);
        }
        java.nio.file.Path targetFile = uploadDir.resolve(filename);
        java.nio.file.Files.copy(tempFile, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            // When
            ResponseEntity<org.springframework.core.io.Resource> response = 
                absenceController.getJustificationFile(filename);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().exists());
            assertTrue(response.getBody().isReadable());
        } finally {
            // Cleanup
            java.nio.file.Files.deleteIfExists(targetFile);
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testGetJustificationFile_NotFound() {
        // Given
        String filename = "nonexistent_file.pdf";

        // When
        ResponseEntity<org.springframework.core.io.Resource> response = 
            absenceController.getJustificationFile(filename);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetJustificationFile_IOException() {
        // Given - Using a filename that would cause IOException during path resolution
        // This is tricky to test directly, but we can test with invalid characters
        String filename = "../../../etc/passwd"; // Path traversal attempt

        // When
        ResponseEntity<org.springframework.core.io.Resource> response = 
            absenceController.getJustificationFile(filename);

        // Then - Should handle gracefully (either not found or internal server error)
        assertNotNull(response);
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND || 
                   response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to create mock MultipartFile
    private org.springframework.web.multipart.MultipartFile createMockMultipartFile(String filename, String contentType) {
        return new org.springframework.web.multipart.MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 1024;
            }

            @Override
            public byte[] getBytes() throws java.io.IOException {
                return "test content".getBytes();
            }

            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                return new java.io.ByteArrayInputStream("test content".getBytes());
            }

            @Override
            public void transferTo(java.io.File dest) throws java.io.IOException, IllegalStateException {
                java.nio.file.Files.write(dest.toPath(), "test content".getBytes());
            }

            @Override
            public void transferTo(java.nio.file.Path dest) throws java.io.IOException, IllegalStateException {
                java.nio.file.Files.write(dest, "test content".getBytes());
            }
        };
    }
}

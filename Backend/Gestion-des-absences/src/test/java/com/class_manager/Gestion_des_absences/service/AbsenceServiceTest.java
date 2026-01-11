package com.class_manager.Gestion_des_absences.service;

import com.class_manager.Gestion_des_absences.client.CreneauClient;
import com.class_manager.Gestion_des_absences.client.MatiereClient;
import com.class_manager.Gestion_des_absences.model.dto.AbsenceResponseDTO;
import com.class_manager.Gestion_des_absences.model.entity.Absence;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.AbsenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbsenceServiceTest {

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private MatiereClient matiereClient;

    @Mock
    private CreneauClient creneauClient;

    @InjectMocks
    private AbsenceService absenceService;

    private Absence testAbsence;
    private Seance testSeance;

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

        testAbsence = new Absence();
        testAbsence.setId(1L);
        testAbsence.setEtudiantId(100L);
        testAbsence.setPresent(false);
        testAbsence.setJustifie(false);
        testAbsence.setSeance(testSeance);
    }

    @Test
    void testGetAbsencesByEtudiantId_Success() {
      
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

     
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

       
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAbsence.getId(), result.get(0).getId());
        assertEquals(testAbsence.getEtudiantId(), result.get(0).getEtudiantId());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_EmptyList() {
     
        Long etudiantId = 100L;
        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(Collections.emptyList());

      
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

      
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testUploadJustification_Success() throws IOException {
    
        Long absenceId = 1L;
        MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

     
        AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);

   
        assertNotNull(result);
        assertFalse(result.isJustifie());
        assertNotNull(result.getFilePath());
        assertNotNull(result.getFileName());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUploadJustification_AbsenceNotFound() {
      
        Long absenceId = 999L;
        MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.empty());

     
        assertThrows(RuntimeException.class, () -> {
            absenceService.uploadJustification(absenceId, file);
        });
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, never()).save(any(Absence.class));
    }

    @Test
    void testUploadJustification_WithExtension() throws IOException {
    
        Long absenceId = 1L;
        MultipartFile file = createMockMultipartFile("document.pdf", "application/pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

     
        AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);

      
        assertNotNull(result);
        assertEquals("document.pdf", result.getFileName());
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUploadJustification_DirectoryCreation() throws IOException {
       
        Long absenceId = 1L;
        MultipartFile file = createMockMultipartFile("test.pdf", "application/pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

      
        AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);

  
        assertNotNull(result);
      
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUpdateJustificationStatus_Accept() {
  
        Long absenceId = 1L;
        boolean justifie = true;

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

      
        AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

    
        assertNotNull(result);
        assertTrue(result.isJustifie());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUpdateJustificationStatus_Reject() throws IOException {
    
        Long absenceId = 1L;
        boolean justifie = false;
        testAbsence.setFilePath("uploads/justifications/test.pdf");
        testAbsence.setFileName("test.pdf");

  
        Path tempFile = Files.createTempFile("test_absence", ".pdf");
        testAbsence.setFilePath(tempFile.toString());

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

   
        AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

   
        assertNotNull(result);
        assertFalse(result.isJustifie());
        assertNull(result.getFilePath());
        assertNull(result.getFileName());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).save(any(Absence.class));

    
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testUpdateJustificationStatus_RejectWithoutFile() {
      
        Long absenceId = 1L;
        boolean justifie = false;
        testAbsence.setFilePath(null);
        testAbsence.setFileName(null);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

     
        AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

      
        assertNotNull(result);
        assertFalse(result.isJustifie());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUpdateJustificationStatus_AbsenceNotFound() {
       
        Long absenceId = 999L;
        boolean justifie = true;

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.empty());

      
        assertThrows(RuntimeException.class, () -> {
            absenceService.updateJustificationStatus(absenceId, justifie);
        });
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, never()).save(any(Absence.class));
    }

    @Test
    void testUploadJustification_NullFilename() throws IOException {
        // Given
        Long absenceId = 1L;
        MultipartFile file = createMockMultipartFile(null, "application/pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

        // When
        AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);

        // Then
        assertNotNull(result);
        assertNull(result.getFileName()); // Should handle null filename gracefully
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUploadJustification_NoExtension() throws IOException {
        // Given
        Long absenceId = 1L;
        MultipartFile file = createMockMultipartFile("document", "application/octet-stream");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

        // When
        AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);

        // Then
        assertNotNull(result);
        assertEquals("document", result.getFileName());
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUpdateJustificationStatus_RejectWithFileDeletionError() throws IOException {
        // Given
        Long absenceId = 1L;
        boolean justifie = false;
        // Create a file path that doesn't exist (will cause deletion to fail silently)
        testAbsence.setFilePath("uploads/justifications/nonexistent_file.pdf");
        testAbsence.setFileName("nonexistent_file.pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

        // When
        AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

        // Then - Should still clear file references even if deletion fails
        assertNotNull(result);
        assertFalse(result.isJustifie());
        assertNull(result.getFilePath());
        assertNull(result.getFileName());
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testGetAbsencesByEtudiantId_WithExternalClientFailure() {
      
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
     
        when(matiereClient.getMatiereById(anyLong())).thenThrow(new RuntimeException("Service unavailable"));
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

       
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

   
        assertNotNull(result);
        assertEquals(1, result.size());
      
        assertNull(result.get(0).getMatiereNom());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithCreneauClientFailure() {
      
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
   
        when(creneauClient.getCreneauById(anyLong())).thenThrow(new RuntimeException("Service unavailable"));

    
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

   
        assertNotNull(result);
        assertEquals(1, result.size());
       
        assertNull(result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithNullMatiere() {
     
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(null);
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

    
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

       
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getMatiereNom());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithMatiereMissingNom() {
      
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
       
        Map<String, Object> matiereWithoutNom = new HashMap<>();
        matiereWithoutNom.put("id", 1L);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(matiereWithoutNom);
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

      
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

     
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getMatiereNom());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithNullCreneau() {
        // Given
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(null);

        // When
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithCreneauMissingFields() {
        // Given
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        // Creneau map without heureDebut/heureFin
        Map<String, Object> creneauWithoutTimes = new HashMap<>();
        creneauWithoutTimes.put("id", 1L);
        when(creneauClient.getCreneauById(anyLong())).thenReturn(creneauWithoutTimes);

        // When
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void testGetAbsencesByEtudiantId_WithAbsenceWithoutSeance() {
      
        Long etudiantId = 100L;
        Absence absenceWithoutSeance = new Absence();
        absenceWithoutSeance.setId(2L);
        absenceWithoutSeance.setEtudiantId(etudiantId);
        absenceWithoutSeance.setPresent(false);
        absenceWithoutSeance.setJustifie(false);
        absenceWithoutSeance.setSeance(null); // No seance

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(Arrays.asList(absenceWithoutSeance));

      
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

       
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getSeanceId());
        verify(absenceRepository, times(1)).findByEtudiantId(etudiantId);
        verify(matiereClient, never()).getMatiereById(anyLong());
        verify(creneauClient, never()).getCreneauById(anyLong());
    }

    @Test
    void testGetAbsencesByEtudiantId_WithSeanceNullMatiereId() {
       
        Long etudiantId = 100L;
        Seance seanceWithoutMatiere = new Seance();
        seanceWithoutMatiere.setId(2L);
        seanceWithoutMatiere.setMatiereId(null);
        seanceWithoutMatiere.setCreneauId(1L);

        Absence absence = new Absence();
        absence.setId(2L);
        absence.setEtudiantId(etudiantId);
        absence.setSeance(seanceWithoutMatiere);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(Arrays.asList(absence));
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

       
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

      
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getMatiereNom());
        verify(matiereClient, never()).getMatiereById(anyLong());
    }

    @Test
    void testGetAbsencesByEtudiantId_WithSeanceNullCreneauId() {
        
        Long etudiantId = 100L;
        Seance seanceWithoutCreneau = new Seance();
        seanceWithoutCreneau.setId(2L);
        seanceWithoutCreneau.setMatiereId(1L);
        seanceWithoutCreneau.setCreneauId(null);

        Absence absence = new Absence();
        absence.setId(2L);
        absence.setEtudiantId(etudiantId);
        absence.setSeance(seanceWithoutCreneau);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(Arrays.asList(absence));
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());

        
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

       
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
        verify(creneauClient, never()).getCreneauById(anyLong());
    }

    @Test
    void testUpdateJustificationStatus_RejectWithFileExists() throws IOException {
   
        Long absenceId = 1L;
        boolean justifie = false;
        
        
        Path tempFile = Files.createTempFile("test_absence_", ".pdf");
        Files.write(tempFile, "test content".getBytes());
        testAbsence.setFilePath(tempFile.toString());
        testAbsence.setFileName("test.pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

        // When
        AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

       
        assertNotNull(result);
        assertFalse(result.isJustifie());
        assertNull(result.getFilePath());
        assertNull(result.getFileName());
        
        assertFalse(Files.exists(tempFile));
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    void testUpdateJustificationStatus_RejectWithFileDeletionIOException() throws IOException {
  
        Long absenceId = 1L;
        boolean justifie = false;
        
      
        Path tempDir = Files.createTempDirectory("test_dir_");
        testAbsence.setFilePath(tempDir.toString());
        testAbsence.setFileName("test.pdf");

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(testAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(testAbsence);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        when(creneauClient.getCreneauById(anyLong())).thenReturn(createCreneauMap());

        try {
          
            AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);

         
            assertNotNull(result);
            assertFalse(result.isJustifie());
            assertNull(result.getFilePath());
            assertNull(result.getFileName());
            verify(absenceRepository, times(1)).save(any(Absence.class));
        } finally {
          
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testGetAbsencesByEtudiantId_WithCreneauMissingHeureDebut() {
      
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        
       
        Map<String, Object> creneauWithoutHeureDebut = new HashMap<>();
        creneauWithoutHeureDebut.put("id", 1L);
        creneauWithoutHeureDebut.put("heureFin", "10:00");
        when(creneauClient.getCreneauById(anyLong())).thenReturn(creneauWithoutHeureDebut);

       
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

       
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getHeureDebut());
        assertEquals("10:00", result.get(0).getHeureFin());
    }

    @Test
    void testGetAbsencesByEtudiantId_WithCreneauMissingHeureFin() {
        // Given
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        
        // Creneau map without heureFin
        Map<String, Object> creneauWithoutHeureFin = new HashMap<>();
        creneauWithoutHeureFin.put("id", 1L);
        creneauWithoutHeureFin.put("heureDebut", "08:00");
        when(creneauClient.getCreneauById(anyLong())).thenReturn(creneauWithoutHeureFin);

        // When
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("08:00", result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
    }

    @Test
    void testGetAbsencesByEtudiantId_WithCreneauMissingBothTimes() {
        // Given
        Long etudiantId = 100L;
        List<Absence> absences = Arrays.asList(testAbsence);

        when(absenceRepository.findByEtudiantId(etudiantId)).thenReturn(absences);
        when(matiereClient.getMatiereById(anyLong())).thenReturn(createMatiereMap());
        
        // Creneau map without both times
        Map<String, Object> creneauWithoutTimes = new HashMap<>();
        creneauWithoutTimes.put("id", 1L);
        when(creneauClient.getCreneauById(anyLong())).thenReturn(creneauWithoutTimes);

        // When
        List<AbsenceResponseDTO> result = absenceService.getAbsencesByEtudiantId(etudiantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getHeureDebut());
        assertNull(result.get(0).getHeureFin());
    }


    private MultipartFile createMockMultipartFile(String filename, String contentType) {
        return new MultipartFile() {
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
            public byte[] getBytes() throws IOException {
                return "test content".getBytes();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new java.io.ByteArrayInputStream("test content".getBytes());
            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {
                Files.write(dest, "test content".getBytes());
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                java.nio.file.Files.write(dest.toPath(), "test content".getBytes());
            }
        };
    }

    private Map<String, Object> createMatiereMap() {
        Map<String, Object> matiere = new HashMap<>();
        matiere.put("nom", "Mathématiques");
        return matiere;
    }

    private Map<String, Object> createCreneauMap() {
        Map<String, Object> creneau = new HashMap<>();
        creneau.put("heureDebut", "08:00");
        creneau.put("heureFin", "10:00");
        return creneau;
    }
}

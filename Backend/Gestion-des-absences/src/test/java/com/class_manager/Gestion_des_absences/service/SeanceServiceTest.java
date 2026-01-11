package com.class_manager.Gestion_des_absences.service;

import com.class_manager.Gestion_des_absences.client.UserClient;
import com.class_manager.Gestion_des_absences.model.dto.*;
import com.class_manager.Gestion_des_absences.model.entity.Role;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.AbsenceRepository;
import com.class_manager.Gestion_des_absences.repository.SeanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeanceServiceTest {

    @Mock
    private SeanceRepository seanceRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    private SeanceService seanceService;

    private UserDTO adminUser;
    private UserDTO teacherUser;
    private Seance testSeance;

    @BeforeEach
    void setUp() {
        adminUser = new UserDTO();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        teacherUser = new UserDTO();
        teacherUser.setId(10L);
        teacherUser.setRole(Role.TEACHER);

        testSeance = new Seance();
        testSeance.setId(1L);
        testSeance.setProfId(10L);
        testSeance.setClasseId(1L);
        testSeance.setCreneauId(1L);
        testSeance.setDate(LocalDate.now());
        testSeance.setMatiereId(1L);
        testSeance.setSalleId(1L);
    }

    @Test
    void testGetSeancesByClasseAndUser_Admin() {
     
        Long classeId = 1L;
        Long userId = 1L;
        List<Seance> seances = Arrays.asList(testSeance);

        when(userClient.getUserById(userId)).thenReturn(adminUser);
        when(seanceRepository.findByClasseId(classeId)).thenReturn(seances);

    
        List<Seance> result = seanceService.getSeancesByClasseAndUser(classeId, userId);

     
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSeance.getId(), result.get(0).getId());
        verify(userClient, times(1)).getUserById(userId);
        verify(seanceRepository, times(1)).findByClasseId(classeId);
        verify(seanceRepository, never()).findByClasseIdAndProfId(anyLong(), anyLong());
    }

    @Test
    void testGetSeancesByClasseAndUser_Teacher() {
    
        Long classeId = 1L;
        Long userId = 10L;
        List<Seance> seances = Arrays.asList(testSeance);

        when(userClient.getUserById(userId)).thenReturn(teacherUser);
        when(seanceRepository.findByClasseIdAndProfId(classeId, userId)).thenReturn(seances);

   
        List<Seance> result = seanceService.getSeancesByClasseAndUser(classeId, userId);

     
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSeance.getId(), result.get(0).getId());
        verify(userClient, times(1)).getUserById(userId);
        verify(seanceRepository, times(1)).findByClasseIdAndProfId(classeId, userId);
        verify(seanceRepository, never()).findByClasseId(anyLong());
    }

    @Test
    void testGetSeancesByClasseAndUser_InvalidUser() {
     
        Long classeId = 1L;
        Long userId = 1L;

        when(userClient.getUserById(userId)).thenReturn(null);

     
        assertThrows(RuntimeException.class, () -> {
            seanceService.getSeancesByClasseAndUser(classeId, userId);
        });
        verify(userClient, times(1)).getUserById(userId);
        verify(seanceRepository, never()).findByClasseId(anyLong());
        verify(seanceRepository, never()).findByClasseIdAndProfId(anyLong(), anyLong());
    }

    @Test
    void testGetSeancesByClasseAndUser_UserWithoutRole() {
     
        Long classeId = 1L;
        Long userId = 1L;
        UserDTO userWithoutRole = new UserDTO();
        userWithoutRole.setId(userId);
        userWithoutRole.setRole(null);

        when(userClient.getUserById(userId)).thenReturn(userWithoutRole);

       
        assertThrows(RuntimeException.class, () -> {
            seanceService.getSeancesByClasseAndUser(classeId, userId);
        });
        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void testGetSeancesByClasseAndUser_DefaultRole() {
     
        Long classeId = 1L;
        Long userId = 1L;
        UserDTO studentUser = new UserDTO();
        studentUser.setId(userId);
        studentUser.setRole(Role.STUDENT);

        when(userClient.getUserById(userId)).thenReturn(studentUser);

     
        List<Seance> result = seanceService.getSeancesByClasseAndUser(classeId, userId);

       
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userClient, times(1)).getUserById(userId);
        verify(seanceRepository, never()).findByClasseId(anyLong());
        verify(seanceRepository, never()).findByClasseIdAndProfId(anyLong(), anyLong());
    }

    @Test
    void testSaveSeanceWithAbsences_Success() {
   
        SeanceDTO request = new SeanceDTO();
        request.setProfId(10L);
        request.setClasseId(1L);
        request.setCreneauId(1L);
        request.setDate("2024-01-15");
        request.setMatiereId(1L);
        request.setSalleId(1L);

        AbsenceDTO absenceDTO = new AbsenceDTO();
        absenceDTO.setEtudiantId(100L);
        absenceDTO.setPresent(false);
        request.setAbsences(Arrays.asList(absenceDTO));

        when(seanceRepository.save(any(Seance.class))).thenReturn(testSeance);

      
        Seance result = seanceService.saveSeanceWithAbsences(request);

      
        assertNotNull(result);
        assertEquals(testSeance.getId(), result.getId());
        verify(seanceRepository, times(1)).save(any(Seance.class));
    }

    @Test
    void testSaveSeanceWithAbsences_WithMultipleAbsences() {
  
        SeanceDTO request = new SeanceDTO();
        request.setProfId(10L);
        request.setClasseId(1L);
        request.setCreneauId(1L);
        request.setDate("2024-01-15");
        request.setMatiereId(1L);
        request.setSalleId(1L);

        AbsenceDTO absence1 = new AbsenceDTO();
        absence1.setEtudiantId(100L);
        absence1.setPresent(true);

        AbsenceDTO absence2 = new AbsenceDTO();
        absence2.setEtudiantId(101L);
        absence2.setPresent(false);

        request.setAbsences(Arrays.asList(absence1, absence2));

        when(seanceRepository.save(any(Seance.class))).thenAnswer(invocation -> {
            Seance seance = invocation.getArgument(0);
            seance.setId(1L);
            return seance;
        });

     
        Seance result = seanceService.saveSeanceWithAbsences(request);

      
        assertNotNull(result);
        assertNotNull(result.getAbsences());
        assertEquals(2, result.getAbsences().size());
        verify(seanceRepository, times(1)).save(any(Seance.class));
    }

    @Test
    void testGetStudentsStatusByClassForProf_Success() {
      
        Long profId = 10L;
        double threshold = 0.25;

        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        StudentDTO student2 = new StudentDTO();
        student2.setId(101L);
        student2.setNiveau("L3");
        student2.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        Object[] absence1 = new Object[]{100L, 5L};
        Object[] absence2 = new Object[]{101L, 1L};
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(Arrays.asList(absence1, absence2));

     
        Object[] total1 = new Object[]{100L, 10L};
        Object[] total2 = new Object[]{101L, 10L};
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(Arrays.asList(total1, total2));

       
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userClient, times(1)).getAllStudents();
        verify(absenceRepository, times(1)).countAbsencesByStudentForProf(profId);
        verify(absenceRepository, times(1)).countTotalSeancesByStudentForProf(profId);
    }

    @Test
    void testGetAbsenceRateByClassAndFiliere_Success() {
    
        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        StudentDTO student2 = new StudentDTO();
        student2.setId(101L);
        student2.setNiveau("L3");
        student2.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        Object[] absence1 = new Object[]{100L, 5L};
        Object[] absence2 = new Object[]{101L, 3L};
        when(absenceRepository.countAbsencesByStudent())
                .thenReturn(Arrays.asList(absence1, absence2));

       
        List<ClassAbsenceRateDTO> result = seanceService.getAbsenceRateByClassAndFiliere();

       
        assertNotNull(result);
        verify(userClient, times(1)).getAllStudents();
        verify(absenceRepository, times(1)).countAbsencesByStudent();
    }

    @Test
    void testGetStudentsStatusByClassForProf_EmptyStudents() {
        // Given
        Long profId = 10L;
        double threshold = 0.25;

        when(userClient.getAllStudents()).thenReturn(Collections.emptyList());
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(Collections.<Object[]>emptyList());
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(Collections.<Object[]>emptyList());

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentsStatusByClassForProf_StudentNotFoundInMap() {
        // Given
        Long profId = 10L;
        double threshold = 0.25;

        when(userClient.getAllStudents()).thenReturn(Collections.emptyList());

        // Student ID in absence data but not in student map
        Object[] absence1 = new Object[]{999L, 5L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(absenceList);

        Object[] total1 = new Object[]{999L, 10L};
        List<Object[]> totalList = new ArrayList<>();
        totalList.add(total1);
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(totalList);

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        // Should skip student not found in map
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentsStatusByClassForProf_StudentWithNoAbsences() {
        // Given
        Long profId = 10L;
        double threshold = 0.25;

        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1));

        // No absences for this student
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(Collections.<Object[]>emptyList());

        Object[] total1 = new Object[]{100L, 10L};
        List<Object[]> totalList = new ArrayList<>();
        totalList.add(total1);
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(totalList);

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Student with 0 absences should be active (rate = 0 < threshold)
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentsStatusByClassForProf_StudentAboveThreshold() {
        // Given
        Long profId = 10L;
        double threshold = 0.25; // 25%

        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1));

        // 5 absences out of 10 seances = 50% (above threshold)
        Object[] absence1 = new Object[]{100L, 5L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(absenceList);

        Object[] total1 = new Object[]{100L, 10L};
        List<Object[]> totalList = new ArrayList<>();
        totalList.add(total1);
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(totalList);

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Student should be marked as inactive
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentsStatusByClassForProf_StudentBelowThreshold() {
        // Given
        Long profId = 10L;
        double threshold = 0.25; // 25%

        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1));

        // 2 absences out of 10 seances = 20% (below threshold)
        Object[] absence1 = new Object[]{100L, 2L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(absenceList);

        Object[] total1 = new Object[]{100L, 10L};
        List<Object[]> totalList = new ArrayList<>();
        totalList.add(total1);
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(totalList);

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Student should be marked as active
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentsStatusByClassForProf_MultipleClasses() {
        // Given
        Long profId = 10L;
        double threshold = 0.25;

        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        StudentDTO student2 = new StudentDTO();
        student2.setId(101L);
        student2.setNiveau("L2");
        student2.setFiliere("GI");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        Object[] absence1 = new Object[]{100L, 5L};
        Object[] absence2 = new Object[]{101L, 3L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        absenceList.add(absence2);
        when(absenceRepository.countAbsencesByStudentForProf(profId))
                .thenReturn(absenceList);

        Object[] total1 = new Object[]{100L, 10L};
        Object[] total2 = new Object[]{101L, 10L};
        List<Object[]> totalList = new ArrayList<>();
        totalList.add(total1);
        totalList.add(total2);
        when(absenceRepository.countTotalSeancesByStudentForProf(profId))
                .thenReturn(totalList);

        // When
        List<StudentsStatusByClassDTO> result = seanceService.getStudentsStatusByClassForProf(profId, threshold);

        // Then
        assertNotNull(result);
        // Should have separate entries for L3-INFO and L2-GI
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetAbsenceRateByClassAndFiliere_EmptyData() {
        // Given
        when(userClient.getAllStudents()).thenReturn(Collections.emptyList());
        when(absenceRepository.countAbsencesByStudent())
                .thenReturn(Collections.<Object[]>emptyList());

        // When
        List<ClassAbsenceRateDTO> result = seanceService.getAbsenceRateByClassAndFiliere();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userClient, times(1)).getAllStudents();
        verify(absenceRepository, times(1)).countAbsencesByStudent();
    }

    @Test
    void testGetAbsenceRateByClassAndFiliere_StudentNotFoundInMap() {
        // Given
        when(userClient.getAllStudents()).thenReturn(Collections.emptyList());

        // Student ID in absence data but not in student map
        Object[] absence1 = new Object[]{999L, 5L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        when(absenceRepository.countAbsencesByStudent())
                .thenReturn(absenceList);

        // When
        List<ClassAbsenceRateDTO> result = seanceService.getAbsenceRateByClassAndFiliere();

        // Then
        assertNotNull(result);
        // Should skip student not found in map
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testGetAbsenceRateByClassAndFiliere_MultipleStudentsSameClass() {
        // Given
        StudentDTO student1 = new StudentDTO();
        student1.setId(100L);
        student1.setNiveau("L3");
        student1.setFiliere("INFO");

        StudentDTO student2 = new StudentDTO();
        student2.setId(101L);
        student2.setNiveau("L3");
        student2.setFiliere("INFO");

        when(userClient.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        Object[] absence1 = new Object[]{100L, 5L};
        Object[] absence2 = new Object[]{101L, 3L};
        List<Object[]> absenceList = new ArrayList<>();
        absenceList.add(absence1);
        absenceList.add(absence2);
        when(absenceRepository.countAbsencesByStudent())
                .thenReturn(absenceList);

        // When
        List<ClassAbsenceRateDTO> result = seanceService.getAbsenceRateByClassAndFiliere();

        // Then
        assertNotNull(result);
        // Should aggregate absences for same class
        verify(userClient, times(1)).getAllStudents();
    }

    @Test
    void testSaveSeanceWithAbsences_EmptyAbsencesList() {
        // Given
        SeanceDTO request = new SeanceDTO();
        request.setProfId(10L);
        request.setClasseId(1L);
        request.setCreneauId(1L);
        request.setDate("2024-01-15");
        request.setMatiereId(1L);
        request.setSalleId(1L);
        request.setAbsences(Collections.emptyList());

        when(seanceRepository.save(any(Seance.class))).thenAnswer(invocation -> {
            Seance seance = invocation.getArgument(0);
            seance.setId(1L);
            return seance;
        });

        // When
        Seance result = seanceService.saveSeanceWithAbsences(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAbsences());
        assertTrue(result.getAbsences().isEmpty());
        verify(seanceRepository, times(1)).save(any(Seance.class));
    }

    @Test
    void testSaveSeanceWithAbsences_NullAbsencesList() {
        // Given
        SeanceDTO request = new SeanceDTO();
        request.setProfId(10L);
        request.setClasseId(1L);
        request.setCreneauId(1L);
        request.setDate("2024-01-15");
        request.setMatiereId(1L);
        request.setSalleId(1L);
        request.setAbsences(null);

        // When/Then - Service should throw NullPointerException when absences is null
        assertThrows(NullPointerException.class, () -> {
            seanceService.saveSeanceWithAbsences(request);
        });
        verify(seanceRepository, never()).save(any(Seance.class));
    }
}

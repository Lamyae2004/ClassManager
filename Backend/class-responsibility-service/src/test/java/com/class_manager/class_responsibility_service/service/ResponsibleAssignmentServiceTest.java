package com.class_manager.class_responsibility_service.service;

import com.class_manager.class_responsibility_service.client.ClasseClient;
import com.class_manager.class_responsibility_service.client.StudentClient;
import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Niveau;
import com.class_manager.class_responsibility_service.model.dto.ClassDTO;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleHistoryDto;
import com.class_manager.class_responsibility_service.model.dto.StudentDto;
import com.class_manager.class_responsibility_service.model.entity.ResponsibleAssignment;
import com.class_manager.class_responsibility_service.repository.ResponsibleAssignmentRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsibleAssignmentServiceTest {

    @Mock
    private ResponsibleAssignmentRepository repository;

    @Mock
    private StudentClient studentClient;

    @Mock
    private ClasseClient classeClient;

    @InjectMocks
    private ResponsibleAssignmentService service;

    private Niveau testNiveau;
    private Filiere testFiliere;
    private ClassDTO testClass;
    private StudentDto testStudent;
    private ResponsibleAssignment testAssignment;

    @BeforeEach
    void setUp() {
        testNiveau = Niveau.CP1;
        testFiliere = Filiere.INFO;

        testClass = new ClassDTO(1L, "CP1-INFO", "INFO");
        testStudent = StudentDto.builder()
                .id(100L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .filiere("INFO")
                .niveau("CP1")
                .build();

        testAssignment = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();
    }

    @Test
    void testAssignRandomResponsible_NewAssignment() {
        // Given
        when(classeClient.getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name()))
                .thenReturn(testClass);
        when(repository.findByClassIdAndActiveTrue(testClass.getId()))
                .thenReturn(Optional.empty());
        when(studentClient.getRandomStudent(testNiveau, testFiliere))
                .thenReturn(testStudent);
        when(repository.save(any(ResponsibleAssignment.class)))
                .thenReturn(testAssignment);

        // When
        StudentDto result = service.assignRandomResponsible(testNiveau, testFiliere);

        // Then
        assertNotNull(result);
        assertEquals(testStudent.getId(), result.getId());
        assertEquals(testStudent.getFirstname(), result.getFirstname());
        verify(classeClient, times(1)).getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name());
        verify(repository, times(1)).findByClassIdAndActiveTrue(testClass.getId());
        verify(studentClient, times(1)).getRandomStudent(testNiveau, testFiliere);
        verify(repository, times(1)).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testAssignRandomResponsible_ExistingActiveAssignment() {
        // Given
        when(classeClient.getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name()))
                .thenReturn(testClass);
        when(repository.findByClassIdAndActiveTrue(testClass.getId()))
                .thenReturn(Optional.of(testAssignment));
        when(studentClient.getStudentById(testAssignment.getStudentId()))
                .thenReturn(testStudent);

        // When
        StudentDto result = service.assignRandomResponsible(testNiveau, testFiliere);

        // Then
        assertNotNull(result);
        assertEquals(testStudent.getId(), result.getId());
        verify(classeClient, times(1)).getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name());
        verify(repository, times(1)).findByClassIdAndActiveTrue(testClass.getId());
        verify(studentClient, times(1)).getStudentById(testAssignment.getStudentId());
        verify(studentClient, never()).getRandomStudent(any(), any());
        verify(repository, never()).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testAssignRandomResponsible_ClassNotFound() {
        // Given
        when(classeClient.getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name()))
                .thenThrow(FeignException.BadRequest.class);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.assignRandomResponsible(testNiveau, testFiliere);
        });
        verify(classeClient, times(1)).getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name());
        verify(repository, never()).findByClassIdAndActiveTrue(anyLong());
    }

    @Test
    void testAssignRandomResponsible_NoStudentAvailable() {
        // Given
        when(classeClient.getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name()))
                .thenReturn(testClass);
        when(repository.findByClassIdAndActiveTrue(testClass.getId()))
                .thenReturn(Optional.empty());
        when(studentClient.getRandomStudent(testNiveau, testFiliere))
                .thenThrow(FeignException.BadRequest.class);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.assignRandomResponsible(testNiveau, testFiliere);
        });
        verify(classeClient, times(1)).getClassByNiveauAndFiliere(testNiveau.name(), testFiliere.name());
        verify(repository, times(1)).findByClassIdAndActiveTrue(testClass.getId());
        verify(studentClient, times(1)).getRandomStudent(testNiveau, testFiliere);
        verify(repository, never()).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testGetActiveAssignmentByClassId_ActiveAssignment() {
        // Given
        Long classId = 1L;
        when(repository.findByClassIdAndActiveTrue(classId))
                .thenReturn(Optional.of(testAssignment));

        // When
        Optional<ResponsibleAssignment> result = service.getActiveAssignmentByClassId(classId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testAssignment.getId(), result.get().getId());
        assertTrue(result.get().isActive());
        verify(repository, times(1)).findByClassIdAndActiveTrue(classId);
        verify(repository, never()).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testGetActiveAssignmentByClassId_ExpiredAssignment() {
        // Given
        Long classId = 1L;
        ResponsibleAssignment expiredAssignment = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(classId)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(1)) // Expired
                .active(true)
                .build();

        when(repository.findByClassIdAndActiveTrue(classId))
                .thenReturn(Optional.of(expiredAssignment));
        when(repository.save(any(ResponsibleAssignment.class)))
                .thenReturn(expiredAssignment);

        // When
        Optional<ResponsibleAssignment> result = service.getActiveAssignmentByClassId(classId);

        // Then
        assertFalse(result.isPresent());
        assertFalse(expiredAssignment.isActive());
        verify(repository, times(1)).findByClassIdAndActiveTrue(classId);
        verify(repository, times(1)).save(expiredAssignment);
    }

    @Test
    void testGetActiveAssignmentByClassId_NoAssignment() {
        // Given
        Long classId = 1L;
        when(repository.findByClassIdAndActiveTrue(classId))
                .thenReturn(Optional.empty());

        // When
        Optional<ResponsibleAssignment> result = service.getActiveAssignmentByClassId(classId);

        // Then
        assertFalse(result.isPresent());
        verify(repository, times(1)).findByClassIdAndActiveTrue(classId);
        verify(repository, never()).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testGetActiveAssignmentByClassId_AssignmentWithNullEndDate() {
        // Given
        Long classId = 1L;
        ResponsibleAssignment assignmentWithNullEndDate = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(classId)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(null) // Null end date
                .active(true)
                .build();

        when(repository.findByClassIdAndActiveTrue(classId))
                .thenReturn(Optional.of(assignmentWithNullEndDate));

        // When
        Optional<ResponsibleAssignment> result = service.getActiveAssignmentByClassId(classId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(assignmentWithNullEndDate.getId(), result.get().getId());
        verify(repository, times(1)).findByClassIdAndActiveTrue(classId);
        verify(repository, never()).save(any(ResponsibleAssignment.class));
    }

    @Test
    void testGetHistory_Success() {
        // Given
        ResponsibleAssignment assignment1 = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(2))
                .active(false)
                .build();

        ResponsibleAssignment assignment2 = ResponsibleAssignment.builder()
                .id(2L)
                .studentId(101L)
                .classId(2L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        ClassDTO class1 = new ClassDTO(1L, "CP1-INFO", "INFO");
        ClassDTO class2 = new ClassDTO(2L, "CP2-INFO", "INFO");

        StudentDto student1 = StudentDto.builder()
                .id(100L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .build();

        StudentDto student2 = StudentDto.builder()
                .id(101L)
                .firstname("Jane")
                .lastname("Smith")
                .email("jane.smith@example.com")
                .build();

        when(repository.findAll()).thenReturn(Arrays.asList(assignment1, assignment2));
        when(classeClient.getClasseById(1L)).thenReturn(class1);
        when(classeClient.getClasseById(2L)).thenReturn(class2);
        when(studentClient.getStudentById(100L)).thenReturn(student1);
        when(studentClient.getStudentById(101L)).thenReturn(student2);

        // When
        List<ResponsibleHistoryDto> result = service.getHistory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        ResponsibleHistoryDto history1 = result.get(0);
        assertEquals(1L, history1.getAssignmentId());
        assertEquals("John", history1.getFirstname());
        assertEquals("Doe", history1.getLastname());
        assertEquals("john.doe@example.com", history1.getEmail());
        assertEquals(1L, history1.getClassId());
        assertEquals("CP1-INFO", history1.getNiveau());
        assertEquals("INFO", history1.getFiliere());
        assertFalse(history1.isActive());

        ResponsibleHistoryDto history2 = result.get(1);
        assertEquals(2L, history2.getAssignmentId());
        assertEquals("Jane", history2.getFirstname());
        assertEquals("Smith", history2.getLastname());
        assertTrue(history2.isActive());

        verify(repository, times(1)).findAll();
        verify(classeClient, times(1)).getClasseById(1L);
        verify(classeClient, times(1)).getClasseById(2L);
        verify(studentClient, times(1)).getStudentById(100L);
        verify(studentClient, times(1)).getStudentById(101L);
    }

    @Test
    void testGetHistory_EmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ResponsibleHistoryDto> result = service.getHistory();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
        verify(classeClient, never()).getClasseById(anyLong());
        verify(studentClient, never()).getStudentById(anyLong());
    }

    @Test
    void testGetHistory_WithMultipleAssignments() {
        // Given
        ResponsibleAssignment assignment1 = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(7))
                .active(false)
                .build();

        ResponsibleAssignment assignment2 = ResponsibleAssignment.builder()
                .id(2L)
                .studentId(101L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(2))
                .active(false)
                .build();

        ResponsibleAssignment assignment3 = ResponsibleAssignment.builder()
                .id(3L)
                .studentId(102L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        ClassDTO class1 = new ClassDTO(1L, "CP1-INFO", "INFO");
        StudentDto student1 = StudentDto.builder().id(100L).firstname("John").lastname("Doe").email("john@example.com").build();
        StudentDto student2 = StudentDto.builder().id(101L).firstname("Jane").lastname("Smith").email("jane@example.com").build();
        StudentDto student3 = StudentDto.builder().id(102L).firstname("Bob").lastname("Johnson").email("bob@example.com").build();

        when(repository.findAll()).thenReturn(Arrays.asList(assignment1, assignment2, assignment3));
        when(classeClient.getClasseById(1L)).thenReturn(class1);
        when(studentClient.getStudentById(100L)).thenReturn(student1);
        when(studentClient.getStudentById(101L)).thenReturn(student2);
        when(studentClient.getStudentById(102L)).thenReturn(student3);

        // When
        List<ResponsibleHistoryDto> result = service.getHistory();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(repository, times(1)).findAll();
        verify(classeClient, times(3)).getClasseById(1L);
        verify(studentClient, times(1)).getStudentById(100L);
        verify(studentClient, times(1)).getStudentById(101L);
        verify(studentClient, times(1)).getStudentById(102L);
    }

    @Test
    void testGetHistory_WithFeignExceptionFromClasseClient() {
        // Given
        ResponsibleAssignment assignment = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        when(repository.findAll()).thenReturn(Arrays.asList(assignment));
        when(classeClient.getClasseById(1L)).thenThrow(new RuntimeException("Service unavailable"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            service.getHistory();
        });
        verify(repository, times(1)).findAll();
        verify(classeClient, times(1)).getClasseById(1L);
        verify(studentClient, never()).getStudentById(anyLong());
    }

    @Test
    void testGetHistory_WithFeignExceptionFromStudentClient() {
        // Given
        ResponsibleAssignment assignment = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        ClassDTO class1 = new ClassDTO(1L, "CP1-INFO", "INFO");

        when(repository.findAll()).thenReturn(Arrays.asList(assignment));
        when(classeClient.getClasseById(1L)).thenReturn(class1);
        when(studentClient.getStudentById(100L)).thenThrow(new RuntimeException("Service unavailable"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            service.getHistory();
        });
        verify(repository, times(1)).findAll();
        verify(classeClient, times(1)).getClasseById(1L);
        verify(studentClient, times(1)).getStudentById(100L);
    }
}

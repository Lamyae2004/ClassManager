package com.class_manager.class_responsibility_service.controller;

import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Niveau;
import com.class_manager.class_responsibility_service.model.dto.AssignResponsibleRequest;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleHistoryDto;
import com.class_manager.class_responsibility_service.model.dto.StudentDto;
import com.class_manager.class_responsibility_service.service.ResponsibleAssignmentService;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsibleAssignmentControllerTest {

    @Mock
    private ResponsibleAssignmentService service;

    @InjectMocks
    private ResponsibleAssignmentController controller;

    private AssignResponsibleRequest request;
    private StudentDto studentDto;
    private ResponsibleHistoryDto historyDto;

    @BeforeEach
    void setUp() {
        request = new AssignResponsibleRequest();
        request.setNiveau(Niveau.CP1);
        request.setFiliere(Filiere.INFO);

        studentDto = StudentDto.builder()
                .id(100L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .filiere("INFO")
                .niveau("CP1")
                .build();

        historyDto = ResponsibleHistoryDto.builder()
                .assignmentId(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .classId(1L)
                .niveau("CP1-INFO")
                .filiere("INFO")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();
    }

    @Test
    void testAssignResponsible_Success() {
        // Given
        when(service.assignRandomResponsible(Niveau.CP1, Filiere.INFO))
                .thenReturn(studentDto);

        // When
        ResponseEntity<StudentDto> response = controller.assignResponsible(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentDto.getId(), response.getBody().getId());
        assertEquals(studentDto.getFirstname(), response.getBody().getFirstname());
        assertEquals(studentDto.getLastname(), response.getBody().getLastname());
        assertEquals(studentDto.getEmail(), response.getBody().getEmail());
        verify(service, times(1)).assignRandomResponsible(Niveau.CP1, Filiere.INFO);
    }

    @Test
    void testAssignResponsible_ServiceThrowsException() {
        // Given
        when(service.assignRandomResponsible(Niveau.CP1, Filiere.INFO))
                .thenThrow(new IllegalArgumentException("Aucune classe trouvée"));

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            controller.assignResponsible(request);
        });
        verify(service, times(1)).assignRandomResponsible(Niveau.CP1, Filiere.INFO);
    }

    @Test
    void testAssignResponsible_DifferentNiveauAndFiliere() {
        // Given
        AssignResponsibleRequest request2 = new AssignResponsibleRequest();
        request2.setNiveau(Niveau.CI3);
        request2.setFiliere(Filiere.CIVIL);

        StudentDto student2 = StudentDto.builder()
                .id(200L)
                .firstname("Alice")
                .lastname("Brown")
                .email("alice.brown@example.com")
                .filiere("CIVIL")
                .niveau("CI3")
                .build();

        when(service.assignRandomResponsible(Niveau.CI3, Filiere.CIVIL))
                .thenReturn(student2);

        // When
        ResponseEntity<StudentDto> response = controller.assignResponsible(request2);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200L, response.getBody().getId());
        assertEquals("Alice", response.getBody().getFirstname());
        assertEquals("CIVIL", response.getBody().getFiliere());
        assertEquals("CI3", response.getBody().getNiveau());
        verify(service, times(1)).assignRandomResponsible(Niveau.CI3, Filiere.CIVIL);
    }

    @Test
    void testAssignResponsible_InvalidRequest() {
        // Given
        AssignResponsibleRequest invalidRequest = new AssignResponsibleRequest();
        // null values
        when(service.assignRandomResponsible(null, null))
                .thenThrow(new IllegalArgumentException("Invalid parameters"));

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            controller.assignResponsible(invalidRequest);
        });
        verify(service, times(1)).assignRandomResponsible(null, null);
    }

    @Test
    void testGetHistory_Success() {
        // Given
        List<ResponsibleHistoryDto> historyList = Arrays.asList(historyDto);
        when(service.getHistory()).thenReturn(historyList);

        // When
        List<ResponsibleHistoryDto> result = controller.getHistory();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        ResponsibleHistoryDto first = result.get(0);
        assertEquals(1L, first.getAssignmentId());
        assertEquals("John", first.getFirstname());
        assertEquals("Doe", first.getLastname());
        assertEquals("john.doe@example.com", first.getEmail());
        assertEquals(1L, first.getClassId());
        assertEquals("CP1-INFO", first.getNiveau());
        assertEquals("INFO", first.getFiliere());
        assertTrue(first.isActive());
        verify(service, times(1)).getHistory();
    }

    @Test
    void testGetHistory_EmptyList() {
        // Given
        when(service.getHistory()).thenReturn(Collections.emptyList());

        // When
        List<ResponsibleHistoryDto> result = controller.getHistory();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service, times(1)).getHistory();
    }

    @Test
    void testGetHistory_MultipleEntries() {
        // Given
        ResponsibleHistoryDto historyDto2 = ResponsibleHistoryDto.builder()
                .assignmentId(2L)
                .firstname("Jane")
                .lastname("Smith")
                .email("jane.smith@example.com")
                .classId(2L)
                .niveau("CP2-INFO")
                .filiere("INFO")
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(2))
                .active(false)
                .build();

        List<ResponsibleHistoryDto> historyList = Arrays.asList(historyDto, historyDto2);
        when(service.getHistory()).thenReturn(historyList);

        // When
        List<ResponsibleHistoryDto> result = controller.getHistory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getAssignmentId());
        assertEquals(2L, result.get(1).getAssignmentId());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
        verify(service, times(1)).getHistory();
    }

    @Test
    void testGetHistory_AllNiveauxAndFilieres() {
        // Given
        ResponsibleHistoryDto cp1 = ResponsibleHistoryDto.builder()
                .assignmentId(1L)
                .firstname("Student1")
                .lastname("Last1")
                .email("s1@example.com")
                .classId(1L)
                .niveau("CP1-INFO")
                .filiere("INFO")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(7))
                .active(false)
                .build();

        ResponsibleHistoryDto ci3 = ResponsibleHistoryDto.builder()
                .assignmentId(2L)
                .firstname("Student2")
                .lastname("Last2")
                .email("s2@example.com")
                .classId(2L)
                .niveau("CI3-CIVIL")
                .filiere("CIVIL")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        List<ResponsibleHistoryDto> historyList = Arrays.asList(cp1, ci3);
        when(service.getHistory()).thenReturn(historyList);

        // When
        List<ResponsibleHistoryDto> result = controller.getHistory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("INFO", result.get(0).getFiliere());
        assertEquals("CIVIL", result.get(1).getFiliere());
        verify(service, times(1)).getHistory();
    }
}

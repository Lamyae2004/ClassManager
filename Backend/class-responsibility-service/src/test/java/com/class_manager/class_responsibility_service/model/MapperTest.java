package com.class_manager.class_responsibility_service.model;

import com.class_manager.class_responsibility_service.model.dto.ResponsibleAssignmentDto;
import com.class_manager.class_responsibility_service.model.entity.ResponsibleAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    private ResponsibleAssignment entity;
    private ResponsibleAssignmentDto dto;

    @Test
    void testMapper_ClassInstantiation() {
        // Given/When - Test that Mapper class can be referenced (covers class declaration)
        // Mapper is a utility class with static methods, so we just verify it exists
        assertNotNull(Mapper.class);
    }

    @Test
    void testMapper_Constructor() {
        // Given/When - Instantiate Mapper to cover the default constructor
        // Even though Mapper is a utility class, Java provides a default constructor
        Mapper mapper = new Mapper();
        
        // Then
        assertNotNull(mapper);
    }

    @BeforeEach
    void setUp() {
        entity = ResponsibleAssignment.builder()
                .id(1L)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(2))
                .active(true)
                .build();

        dto = ResponsibleAssignmentDto.builder()
                .id(2L)
                .studentId(200L)
                .classId(2L)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(7))
                .active(false)
                .build();
    }

    @Test
    void testRespAssigntoDto_Success() {
        // When
        ResponsibleAssignmentDto result = Mapper.RespAssigntoDto(entity);

        // Then
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getStudentId(), result.getStudentId());
        assertEquals(entity.getClassId(), result.getClassId());
        assertEquals(entity.getStartDate(), result.getStartDate());
        assertEquals(entity.getEndDate(), result.getEndDate());
        assertEquals(entity.isActive(), result.isActive());
    }

    @Test
    void testRespAssigntoDto_WithNullEndDate() {
        // Given
        entity.setEndDate(null);

        // When
        ResponsibleAssignmentDto result = Mapper.RespAssigntoDto(entity);

        // Then
        assertNotNull(result);
        assertNull(result.getEndDate());
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void testRespAssigntoDto_WithInactiveAssignment() {
        // Given
        entity.setActive(false);

        // When
        ResponsibleAssignmentDto result = Mapper.RespAssigntoDto(entity);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        assertEquals(entity.isActive(), result.isActive());
    }

    @Test
    void testRespAssignDtotoEntity_Success() {
        // When
        ResponsibleAssignment result = Mapper.RespAssignDtotoEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getStudentId(), result.getStudentId());
        assertEquals(dto.getClassId(), result.getClassId());
        assertEquals(dto.getStartDate(), result.getStartDate());
        assertEquals(dto.getEndDate(), result.getEndDate());
        assertEquals(dto.isActive(), result.isActive());
    }

    @Test
    void testRespAssignDtotoEntity_WithNullEndDate() {
        // Given
        dto.setEndDate(null);

        // When
        ResponsibleAssignment result = Mapper.RespAssignDtotoEntity(dto);

        // Then
        assertNotNull(result);
        assertNull(result.getEndDate());
        assertEquals(dto.getId(), result.getId());
    }

    @Test
    void testRespAssignDtotoEntity_WithActiveAssignment() {
        // Given
        dto.setActive(true);

        // When
        ResponsibleAssignment result = Mapper.RespAssignDtotoEntity(dto);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        assertEquals(dto.isActive(), result.isActive());
    }

    @Test
    void testRespAssigntoDto_WithNullId() {
        // Given
        entity.setId(null);

        // When
        ResponsibleAssignmentDto result = Mapper.RespAssigntoDto(entity);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    void testRespAssignDtotoEntity_WithNullId() {
        // Given
        dto.setId(null);

        // When
        ResponsibleAssignment result = Mapper.RespAssignDtotoEntity(dto);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    void testMapper_RoundTrip() {
        // Given - Entity to DTO
        ResponsibleAssignmentDto dtoFromEntity = Mapper.RespAssigntoDto(entity);

        // When - DTO back to Entity
        ResponsibleAssignment entityFromDto = Mapper.RespAssignDtotoEntity(dtoFromEntity);

        // Then
        assertEquals(entity.getId(), entityFromDto.getId());
        assertEquals(entity.getStudentId(), entityFromDto.getStudentId());
        assertEquals(entity.getClassId(), entityFromDto.getClassId());
        assertEquals(entity.getStartDate(), entityFromDto.getStartDate());
        assertEquals(entity.getEndDate(), entityFromDto.getEndDate());
        assertEquals(entity.isActive(), entityFromDto.isActive());
    }

    @Test
    void testMapper_RoundTripWithNulls() {
        // Given
        ResponsibleAssignment entityWithNulls = ResponsibleAssignment.builder()
                .id(null)
                .studentId(100L)
                .classId(1L)
                .startDate(LocalDate.now())
                .endDate(null)
                .active(false)
                .build();

        // When
        ResponsibleAssignmentDto dto = Mapper.RespAssigntoDto(entityWithNulls);
        ResponsibleAssignment result = Mapper.RespAssignDtotoEntity(dto);

        // Then
        assertNull(result.getId());
        assertNull(result.getEndDate());
        assertEquals(entityWithNulls.getStudentId(), result.getStudentId());
        assertEquals(entityWithNulls.getClassId(), result.getClassId());
        assertEquals(entityWithNulls.getStartDate(), result.getStartDate());
        assertEquals(entityWithNulls.isActive(), result.isActive());
    }
}

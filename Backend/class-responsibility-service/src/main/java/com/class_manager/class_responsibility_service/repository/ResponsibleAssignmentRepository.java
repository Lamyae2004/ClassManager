package com.class_manager.class_responsibility_service.repository;

import com.class_manager.class_responsibility_service.model.entity.ResponsibleAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ResponsibleAssignmentRepository extends JpaRepository<ResponsibleAssignment,Long> {
    Optional<ResponsibleAssignment> findByClassIdAndActiveTrue(Long classId);
}

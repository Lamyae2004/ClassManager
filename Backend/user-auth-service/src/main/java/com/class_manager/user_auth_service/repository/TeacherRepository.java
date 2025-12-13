package com.class_manager.user_auth_service.repository;

import com.class_manager.user_auth_service.model.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
}

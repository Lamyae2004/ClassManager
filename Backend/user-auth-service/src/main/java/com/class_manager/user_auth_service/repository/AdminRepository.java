package com.class_manager.user_auth_service.repository;
import com.class_manager.user_auth_service.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}

package com.class_manager.user_auth_service.repository;

import com.class_manager.user_auth_service.model.entity.Filiere;
import com.class_manager.user_auth_service.model.entity.Niveau;
import com.class_manager.user_auth_service.model.entity.Role;
import com.class_manager.user_auth_service.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Integer>{
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email,String password);

    boolean existsByRole(Role role);
    long countByRole(Role role);

    long countByRoleAndIsActivated(Role role, boolean isActivated);

    @Query("SELECT CONCAT(s.filiere, s.niveau) AS className, COUNT(s) AS studentCount " +
            "FROM Student s GROUP BY s.filiere, s.niveau")
    List<Object[]> countStudentsPerClass();


    @Query("""
    SELECT COUNT(s)
    FROM Student s
    WHERE s.niveau = :niveau
    AND s.filiere = :filiere
      AND s.isActivated = :activated
    """)
    int countByNiveauAndFiliereAndActivated(
            @Param("niveau") Niveau niveau,
            @Param("filiere") Filiere filiere,
            @Param("activated") boolean activated
    );



}

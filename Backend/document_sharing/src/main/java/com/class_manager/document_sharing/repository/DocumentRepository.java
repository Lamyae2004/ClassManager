package com.class_manager.document_sharing.repository;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByClasseId(Long classeId);

    List<DocumentEntity> findByClasseIdAndModuleId(Long classeId, Long moduleId);

    List<DocumentEntity> findByClasseIdAndType(Long classeId, DocumentType type);

    List<DocumentEntity> findByClasseIdAndModuleIdAndType(
            Long classeId, Long moduleId, DocumentType type
    );
}

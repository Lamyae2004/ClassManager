package com.class_manager.document_sharing.model.entity;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Data
@Table(name = "shared_documents")
@Builder
@AllArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime uploadDate;


    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private Long classeId;

    @Column(nullable = false)
    private Long profId;

    public DocumentEntity toDocumentEntity(DocumentDto d){
        return DocumentEntity.builder()
                .id(d.getId())
                .title(d.getTitle())
                .type(d.getType())
                .fileName(d.getFileName())
                .uploadDate(d.getUploadDate())
                .moduleId(d.getModuleId())
                .classeId(d.getClasseId())
                .profId(d.getProfId())
                .build();
    }

}

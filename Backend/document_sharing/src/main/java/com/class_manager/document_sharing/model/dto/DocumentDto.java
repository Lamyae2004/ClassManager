package com.class_manager.document_sharing.model.dto;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DocumentDto {
    private Long id;
    private String title;
    private DocumentType type;

    private String fileName;
    private Long fileSize;
    private LocalDateTime uploadDate;

    private Long moduleId;
    private Long classeId;
    private Long profId;
    private String fileUrl;

    public DocumentDto toDocumentDto(DocumentEntity d){
        return DocumentDto.builder()
                .id(d.getId())
                .title(d.getTitle())
                .type(d.getType())
                .fileName(d.getFileName())
                .uploadDate(d.getUploadDate())
                .moduleId(d.getModuleId())
                .classeId(d.getClasseId())
                .profId(d.getProfId())
                .fileUrl(d.getFileUrl())
                .build();
    }
}

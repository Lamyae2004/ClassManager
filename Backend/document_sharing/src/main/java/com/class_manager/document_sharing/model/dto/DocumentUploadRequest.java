package com.class_manager.document_sharing.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentUploadRequest {

    private String title;
    private String type;
    private Long moduleId;
    private Long classeId;
    private Long profId;
    private String fileName;
    private MultipartFile file;
}

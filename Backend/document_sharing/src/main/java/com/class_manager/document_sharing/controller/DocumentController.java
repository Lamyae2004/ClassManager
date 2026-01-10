package com.class_manager.document_sharing.controller;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import com.class_manager.document_sharing.model.dto.DocumentUploadRequest;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import com.class_manager.document_sharing.repository.DocumentRepository;
import com.class_manager.document_sharing.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentRepository repository;
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @ModelAttribute DocumentUploadRequest request
    ) throws IOException {

        documentService.uploadDocument(request);

        return ResponseEntity.ok("Document envoyé avec succès");
    }




}

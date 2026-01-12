package com.class_manager.document_sharing.controller;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import com.class_manager.document_sharing.model.dto.DocumentUploadRequest;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import com.class_manager.document_sharing.repository.DocumentRepository;
import com.class_manager.document_sharing.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Document", description = "Gestion des documents")
@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentRepository repository;
    private final DocumentService documentService;

    @Operation(summary = "Télécharger un fichier", description = "Récupère un document par son nom de fichier")
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {

        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }



    @Operation(summary = "Lister les documents", description = "Liste tous les documents filtrables par classe, module et type")
    @GetMapping
    public List<DocumentDto> getDocuments(
            @RequestParam Long classeId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) DocumentType type
    ) {
        return documentService.getDocuments(classeId, moduleId, type);
    }

    @Operation(summary = "Uploader un document", description = "Envoie un fichier avec ses informations (classe, module, type)")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @ModelAttribute DocumentUploadRequest request
    ) throws IOException {

        documentService.uploadDocument(request);

        return ResponseEntity.ok("Document envoyé avec succès");
    }




}

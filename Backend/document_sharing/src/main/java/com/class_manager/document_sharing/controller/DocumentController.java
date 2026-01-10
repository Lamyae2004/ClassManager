package com.class_manager.document_sharing.controller;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import com.class_manager.document_sharing.repository.DocumentRepository;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("moduleId") Long moduleId,
            @RequestParam("classeId") Long classeId,
            @RequestParam("profId") Long profId,
            @RequestParam("fileName") String fileName,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        // Chemin relatif vers le dossier uploads dans le projet
        String uploadDirPath = new File(".").getAbsolutePath() + "/uploads";
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) uploadDir.mkdirs(); // créer si n'existe pas

        String safeFileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        // Remplacer les caractères invalides Windows par "_"

        File dest = new File(uploadDir, safeFileName);

        // Copier le contenu du MultipartFile directement
        try (InputStream in = file.getInputStream()) {
            java.nio.file.Files.copy(in, dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // Construire DTO et Entity comme avant
        DocumentEntity entity = DocumentEntity.builder()
                .title(title)
                .type(DocumentType.valueOf(type.toUpperCase()))
                .fileName(safeFileName)
                .fileUrl("uploads/" + safeFileName) // chemin relatif pour l'accès depuis ton app
                .uploadDate(LocalDateTime.now())
                .moduleId(moduleId)
                .classeId(classeId)
                .profId(profId)
                .build();

        repository.save(entity);

        return ResponseEntity.ok("Document envoyé avec succès");
    }



}

package com.class_manager.document_sharing.service;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentUploadRequest;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import com.class_manager.document_sharing.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;

    public void uploadDocument(DocumentUploadRequest request) throws IOException {

        String originalFileName = request.getFileName();
        String uniqueFileName = generateUniqueFileName(originalFileName);

        File destination = prepareUploadDirectory(uniqueFileName);
        saveFile(request.getFile(), destination);

        DocumentEntity entity = buildDocumentEntity(request, uniqueFileName);

        repository.save(entity);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";

        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        return java.util.UUID.randomUUID() + extension;
    }

    private File prepareUploadDirectory(String fileName) {
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return new File(uploadDir, fileName);
    }

    private void saveFile(MultipartFile file, File destination) throws IOException {
        try (InputStream in = file.getInputStream()) {
            java.nio.file.Files.copy(
                    in,
                    destination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private DocumentEntity buildDocumentEntity(
            DocumentUploadRequest request,
            String uniqueFileName
    ) {
        return DocumentEntity.builder()
                .title(request.getTitle())
                .type(DocumentType.valueOf(request.getType().toUpperCase()))
                .fileName(request.getFileName())
                .fileUrl("uploads/" + uniqueFileName)
                .uploadDate(LocalDateTime.now())
                .moduleId(request.getModuleId())
                .classeId(request.getClasseId())
                .profId(request.getProfId())
                .build();
    }
}

package com.class_manager.document_sharing.service;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import com.class_manager.document_sharing.model.dto.DocumentUploadRequest;
import com.class_manager.document_sharing.model.entity.DocumentEntity;
import com.class_manager.document_sharing.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private DocumentService service;

    @Test
    void getDocuments_byClasseId_shouldReturnDTOs() {
        DocumentEntity doc = DocumentEntity.builder()
                .id(1L)
                .title("Cours Java")
                .type(DocumentType.COURS)
                .fileName("cours_java.pdf")
                .fileUrl("uploads/cours_java.pdf")
                .classeId(1L)
                .build();

        when(repository.findByClasseId(1L)).thenReturn(List.of(doc));

        List<DocumentDto> result = service.getDocuments(1L, null, null);

        assertEquals(1, result.size());
        assertEquals("Cours Java", result.get(0).getTitle());
        assertEquals(DocumentType.COURS, result.get(0).getType());
    }

    @Test
    void getDocuments_byClasseIdAndType_shouldReturnDTOs() {
        DocumentEntity doc = DocumentEntity.builder()
                .id(2L)
                .title("TP Java")
                .type(DocumentType.TP)
                .fileName("tp_java.pdf")
                .fileUrl("uploads/tp_java.pdf")
                .classeId(1L)
                .build();

        when(repository.findByClasseIdAndType(1L, DocumentType.TP))
                .thenReturn(List.of(doc));

        List<DocumentDto> result = service.getDocuments(1L, null, DocumentType.TP);

        assertEquals(1, result.size());
        assertEquals("TP Java", result.get(0).getTitle());
        assertEquals(DocumentType.TP, result.get(0).getType());
    }

    @Test
    void uploadDocument_shouldCallRepositorySave() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "Dummy content".getBytes()
        );

        DocumentUploadRequest request = new DocumentUploadRequest();
        request.setTitle("TD Java");
        request.setType("TD");
        request.setFile(file);
        request.setFileName("test.pdf");
        request.setClasseId(1L);
        request.setModuleId(2L);
        request.setProfId(3L);


        service.uploadDocument(request);

        // Vérifie que repository.save a été appelé une fois
        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(repository, times(1)).save(captor.capture());

        DocumentEntity saved = captor.getValue();
        assertEquals("TD Java", saved.getTitle());
        assertEquals(DocumentType.TD, saved.getType());
        assertEquals(1L, saved.getClasseId());
    }
}

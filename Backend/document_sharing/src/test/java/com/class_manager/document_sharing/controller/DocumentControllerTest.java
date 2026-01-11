package com.class_manager.document_sharing.controller;

import com.class_manager.document_sharing.model.DocumentType;
import com.class_manager.document_sharing.model.dto.DocumentDto;
import com.class_manager.document_sharing.model.dto.DocumentUploadRequest;
import com.class_manager.document_sharing.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService service;

    @InjectMocks
    private DocumentController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getDocuments_shouldReturnDTOs() throws Exception {
        DocumentDto dto = new DocumentDto();
        dto.setTitle("TP Java");
        dto.setType(DocumentType.TP);

        when(service.getDocuments(1L, null, null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/document")
                        .param("classeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("TP Java"))
                .andExpect(jsonPath("$[0].type").value("TP"));
    }

    @Test
    void uploadDocument_shouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "td.pdf", "application/pdf", "Dummy content".getBytes()
        );

        // On ne teste pas la logique du service ici, juste le controller
        doNothing().when(service).uploadDocument(any(DocumentUploadRequest.class));

        mockMvc.perform(multipart("/api/document/upload")
                        .file(file)
                        .param("title", "TD Java")
                        .param("type", "TD")
                        .param("classeId", "1")
                        .param("moduleId", "2")
                        .param("profId", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Document envoyé avec succès"));
    }

    @Test
    void downloadFile_shouldReturnResource() throws Exception {

        Resource resource = new ByteArrayResource("Dummy content".getBytes());
    }
}

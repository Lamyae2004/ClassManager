package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.client.EtudiantClient;
import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.EmploiDuTempsDTO;
import com.class_manager.Gestion_des_emplois.service.EmploiImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class EmploiImportControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EmploiImportService emploiService;

    @Mock
    private TeacherClient teacherClient;

    @Mock
    private EtudiantClient etudiantClient;

    @InjectMocks
    private EmploiImportController emploiImportController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Configure MockMvc pour utiliser le controller avec les mocks injectés
        mockMvc = MockMvcBuilders.standaloneSetup(emploiImportController).build();
    }


    @Test
    void getMyClasses_shouldReturnCount() throws Exception {
        long userId = 11L;
        when(emploiService.getMyClassesCount(userId)).thenReturn(3);

        mockMvc.perform(get("/emploi/my-classes/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.myClasses").value(3));
    }

    @Test
    void getEmploiByClasse_shouldReturnList() throws Exception {
        long classeId = 11L;
        EmploiDuTempsDTO dto = new EmploiDuTempsDTO();
        dto.setId(1L);
        dto.setMatiereNom("Communication mobile");

        when(emploiService.getEmploiByClasse(classeId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/emploi/classe/" + classeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matiereNom").value("Communication mobile"));
    }
}
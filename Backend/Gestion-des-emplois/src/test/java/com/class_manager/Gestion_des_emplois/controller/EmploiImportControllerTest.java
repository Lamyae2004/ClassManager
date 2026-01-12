package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.client.EtudiantClient;
import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.EmploiDuTempsDTO;
import com.class_manager.Gestion_des_emplois.model.dto.EmploiProfDTO;
import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    @Test
    void getStudentsStatusByClass_shouldReturnList() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("classe", "CI1");
        map.put("activeStudents", 10);

        when(emploiService.getStudentsStatusPerClass(1L))
                .thenReturn(List.of(map));

        mockMvc.perform(get("/emploi/students-status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].classe").value("CI1"));
    }

    @Test
    void createEmploi_shouldReturnSavedEmploi() throws Exception {
        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(1L);

        when(emploiService.createEmploi(any())).thenReturn(e);

        mockMvc.perform(post("/emploi/create")
                        .contentType("application/json")
                        .content("{\"jour\":\"Lundi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void getEmploiByClasseProfJour_shouldReturnList() throws Exception {
        EmploiDuTempsDTO dto = new EmploiDuTempsDTO();
        dto.setJour("Lundi");

        when(emploiService.getEmploiByClasseProfJour(1L,2L,"Lundi"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/emploi/classe/1/prof/2/jour/Lundi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jour").value("Lundi"));
    }


    @Test
    void getEmploiProf_shouldReturnList() throws Exception {
        EmploiProfDTO dto = new EmploiProfDTO();
        dto.setMatiereNom("Communication mobile");

        when(emploiService.getEmploiDuJourForProf(5L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/emploi/prof/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matiereNom").value("Communication mobile"));
    }

    @Test
    void deleteEmploi_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/emploi/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEmploiForStudent_shouldReturnList() throws Exception {
        EmploiDuTempsDTO dto = new EmploiDuTempsDTO();
        dto.setClasseNom("CI1");

        when(emploiService.getEmploiForStudent("CI1", "MECA", null))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/emploi/student")
                        .param("classe", "CI1")
                        .param("filiere", "MECA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].classeNom").value("CI1"));
    }

    @Test
    void getMatieresEtProfs_shouldReturnList() throws Exception {
        when(emploiService.getMatieresEtProfs("CI2", Filiere.MECA))
                .thenReturn(List.of());

        mockMvc.perform(get("/emploi/classes/matieres-profs")
                        .param("niveau", "CI2")
                        .param("filiere", "MECA"))
                .andExpect(status().isOk());
    }

    @Test
    void importEmploi_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/emploi/import")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEmplois_shouldReturnList() throws Exception {
        when(emploiService.getAllEmplois()).thenReturn(List.of());

        mockMvc.perform(get("/emploi"))
                .andExpect(status().isOk());
    }

    @Test
    void getEmploiWithTeacher_shouldReturnData() throws Exception {
        EmploiDuTemps e = new EmploiDuTemps(); e.setId(1L); e.setProfId(2L);
        when(emploiService.getEmploiById(1L)).thenReturn(Optional.of(e));

        TeacherDTO t = new TeacherDTO(); t.setFirstname("Ali"); t.setLastname("Benali");
        when(teacherClient.getTeacherById(2L)).thenReturn(t);

        mockMvc.perform(get("/emploi/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emploi.id").value(1));
    }

    @Test
    void deleteEmploiGroup_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/emploi/group")
                        .param("classe", "CI2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateEmploiCell_shouldReturnUpdated() throws Exception {
        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(1L);

        when(emploiService.updateEmploiCell(eq(1L), any())).thenReturn(e);

        mockMvc.perform(put("/emploi/1/cell")
                        .contentType("application/json")
                        .content("{\"matiere\":\"Math\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }





}
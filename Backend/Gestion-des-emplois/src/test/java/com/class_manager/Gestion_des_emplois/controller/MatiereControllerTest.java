package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import com.class_manager.Gestion_des_emplois.repository.MatiereRepository;
import com.class_manager.Gestion_des_emplois.service.EmploiImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MatiereControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatiereRepository matiereRepository;

    @Mock
    private EmploiImportService emploiImportService;

    @InjectMocks
    private MatiereController matiereController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(matiereController).build();
    }

    // =========================
    // GET /emploi/matieres
    // =========================
    @Test
    void getAll_shouldReturnMatieres() throws Exception {
        Matiere m = new Matiere();
        m.setId(1L);
        m.setNom("Math");

        when(matiereRepository.findAll()).thenReturn(List.of(m));

        mockMvc.perform(get("/emploi/matieres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Math"));
    }

    // =========================
    // GET /emploi/matieres/{id}
    // =========================
    @Test
    void getMatiereById_shouldReturnMatiere() throws Exception {
        Matiere m = new Matiere();
        m.setId(2L);
        m.setNom("Physique");

        when(matiereRepository.findById(2L)).thenReturn(Optional.of(m));

        mockMvc.perform(get("/emploi/matieres/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Physique"));
    }

    // =========================
    // GET /emploi/matieres/classe/{id}
    // =========================
    @Test
    void getMatieresByClasse_shouldReturnList() throws Exception {
        Matiere m = new Matiere();
        m.setId(3L);
        m.setNom("Algo");

        when(emploiImportService.getMatieresByClasse(1L))
                .thenReturn(List.of(m));

        mockMvc.perform(get("/emploi/matieres/classe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Algo"));
    }

    // =========================
    // GET /emploi/matieres/classe/prof/{classeId}/{profId}
    // =========================
    @Test
    void getMatieresByClasseAndProf_shouldReturnList() throws Exception {
        Matiere m = new Matiere();
        m.setId(4L);
        m.setNom("Réseaux");

        when(emploiImportService.getMatieresByClasseAndProf(1L, 5L))
                .thenReturn(List.of(m));

        mockMvc.perform(get("/emploi/matieres/classe/prof/1/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Réseaux"));
    }
}

package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.dto.ClasseDTO;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import com.class_manager.Gestion_des_emplois.service.ClasseService;
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

class ClasseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClasseRepository classeRepository;

    @Mock
    private ClasseService classeService;

    @InjectMocks
    private ClasseController classeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(classeController).build();
    }

    // =========================
    // GET /emploi/classes
    // =========================
    @Test
    void getAll_shouldReturnClasses() throws Exception {
        Classe c = new Classe();
        c.setId(1L);
        c.setNom("CI1");
        c.setFiliere(Filiere.MECA);

        when(classeRepository.findAll()).thenReturn(List.of(c));

        mockMvc.perform(get("/emploi/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("CI1"));
    }

    // =========================
    // GET /emploi/classes/prof/{id}
    // =========================
    @Test
    void getClassesByProfId_shouldReturnDTOs() throws Exception {
        ClasseDTO dto = new ClasseDTO(1L, "CI2", "MECA");

        when(classeService.getClassesByTeacherId(5L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/emploi/classes/prof/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("CI2"));
    }

    // =========================
    // GET /emploi/classes/search
    // =========================
    @Test
    void getClassByNiveauAndFiliere_shouldReturnClasse() throws Exception {
        ClasseDTO dto = new ClasseDTO(2L, "CI1", "MECA");

        when(classeService.getClassByNiveauAndFiliere("CI1", "MECA"))
                .thenReturn(dto);

        mockMvc.perform(get("/emploi/classes/search")
                        .param("niveau", "CI1")
                        .param("filiere", "MECA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("CI1"));
    }

    // =========================
    // GET /emploi/classes/{id}
    // =========================
    @Test
    void getClasseById_shouldReturnClasse() throws Exception {
        ClasseDTO dto = new ClasseDTO(3L, "CI3", "INFO");

        when(classeService.getClassById(3L))
                .thenReturn(dto);

        mockMvc.perform(get("/emploi/classes/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("CI3"));
    }
}

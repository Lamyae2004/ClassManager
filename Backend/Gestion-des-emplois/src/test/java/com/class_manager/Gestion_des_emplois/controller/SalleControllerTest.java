package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Salle;
import com.class_manager.Gestion_des_emplois.repository.SalleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SalleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalleRepository salleRepository;

    @InjectMocks
    private SalleController salleController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salleController).build();
    }

    // ======================
    // GET /salles
    // ======================
    @Test
    void getAll_shouldReturnListOfSalles() throws Exception {
        Salle s = new Salle();
        s.setId(1L);
        s.setNom("Salle A");

        when(salleRepository.findAll()).thenReturn(List.of(s));

        mockMvc.perform(get("/salles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Salle A"));
    }
}

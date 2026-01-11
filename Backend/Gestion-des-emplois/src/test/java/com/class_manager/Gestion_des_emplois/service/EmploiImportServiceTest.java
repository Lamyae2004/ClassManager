package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.entity.*;
import com.class_manager.Gestion_des_emplois.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmploiImportServiceTest {
    @Mock
    private ClasseRepository classeRepo;
    @Mock
    private MatiereRepository matiereRepo;
    @Mock
    private SalleRepository salleRepo;
    @Mock
    private CreneauRepository creneauRepo;
    @Mock
    private EmploiDuTempsRepository edtRepo;
    @Mock
    private TeacherClient teacherClient;

    @InjectMocks
    private EmploiImportService service;

    @Test
    void getMyClassesCount_shouldReturnNumberOfDistinctClasses() {
        Classe c1 = new Classe(); c1.setNom("GI1");
        Classe c2 = new Classe(); c2.setNom("GI2");

        when(edtRepo.findDistinctClassesByProfId(1L))
                .thenReturn(List.of(c1, c2));

        int count = service.getMyClassesCount(1L);

        assertEquals(2, count);
    }

    @Test
    void getEmploiByClasse_shouldReturnDTOList() {
        Classe c = new Classe(); c.setId(1L); c.setNom("GI1");
        Matiere m = new Matiere(); m.setId(1L); m.setNom("Communication mobile");
        Salle s = new Salle(); s.setId(1L); s.setNom("S1");
        Creneau cr = new Creneau(); cr.setId(1L); cr.setHeureDebut("08:00"); cr.setHeureFin("10:00");

        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(5L); e.setJour("Lundi");
        e.setClasse(c); e.setMatiere(m); e.setSalle(s); e.setCreneau(cr);
        e.setProfId(10L);

        when(edtRepo.findByClasseId(1L)).thenReturn(List.of(e));

        var res = service.getEmploiByClasse(1L);

        assertEquals(1, res.size());
        assertEquals("Communication mobile", res.get(0).getMatiereNom());
    }
}

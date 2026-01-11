package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.EmploiCellUpdateDTO;
import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import com.class_manager.Gestion_des_emplois.model.entity.*;
import com.class_manager.Gestion_des_emplois.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    void getEmploiDuJourForProf_shouldReturnEmploisForProf() {
        Classe c = new Classe(); c.setNom("CI2"); c.setFiliere(Filiere.MECA);
        Matiere m = new Matiere(); m.setNom("Math");
        Salle s = new Salle(); s.setNom("S1");
        Creneau cr = new Creneau(); cr.setHeureDebut("08:00"); cr.setHeureFin("10:00");

        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(1L); e.setProfId(5L); e.setJour("Lundi");
        e.setClasse(c); e.setMatiere(m); e.setSalle(s); e.setCreneau(cr);

        when(edtRepo.findAll()).thenReturn(List.of(e));

        var res = service.getEmploiDuJourForProf(5L);

        assertEquals(1, res.size());
        assertEquals("Math", res.get(0).getMatiereNom());
    }

    @Test
    void getEmploiByClasseProfJour_shouldFilterCorrectly() {
        Classe c = new Classe(); c.setId(1L);
        Matiere m = new Matiere(); m.setNom("Math");

        EmploiDuTemps e = new EmploiDuTemps();
        e.setClasse(c); e.setProfId(2L); e.setJour("Lundi"); e.setMatiere(m);

        when(edtRepo.findAll()).thenReturn(List.of(e));

        var res = service.getEmploiByClasseProfJour(1L, 2L, "Lundi");

        assertEquals(1, res.size());
    }

    @Test
    void deleteEmploi_shouldCallRepository() {
        service.deleteEmploi(5L);
        verify(edtRepo).deleteById(5L);
    }

    @Test
    void createEmploi_shouldSaveEmploi() {
        Classe c = new Classe(); c.setNom("CI2");
        Matiere m = new Matiere(); m.setNom("Math");

        EmploiDuTemps e = new EmploiDuTemps();
        e.setClasse(c); e.setMatiere(m);

        when(classeRepo.findByNom("CI2")).thenReturn(List.of(c));
        when(matiereRepo.findByNom("Math")).thenReturn(Optional.of(m));
        when(edtRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        EmploiDuTemps res = service.createEmploi(e);

        assertEquals("CI2", res.getClasse().getNom());
    }

    @Test
    void getAllEmplois_shouldReturnAll() {
        when(edtRepo.findAll()).thenReturn(List.of(new EmploiDuTemps()));

        var res = service.getAllEmplois();

        assertEquals(1, res.size());
    }


    @Test
    void getEmploisByGroup_shouldReturnMatchingGroup() {
        Classe c = new Classe(); c.setNom("CI2"); c.setFiliere(Filiere.MECA);
        EmploiDuTemps e = new EmploiDuTemps(); e.setClasse(c); e.setSemestre(Semestre.S1);

        when(edtRepo.findAll()).thenReturn(List.of(e));

        var res = service.getEmploisByGroup("CI2", "MECA", "S1");

        assertEquals(1, res.size());
    }



    @Test
    void getMatieresEtProfs_shouldReturnDistinctMatieresAndProfs() {
        Classe c = new Classe(); c.setNom("CI2"); c.setFiliere(Filiere.MECA);
        Matiere m = new Matiere(); m.setId(1L); m.setNom("Communication mobile");

        EmploiDuTemps e = new EmploiDuTemps();
        e.setClasse(c); e.setMatiere(m); e.setProfId(5L);

        when(edtRepo.findByClasseNomAndFiliere("CI2", Filiere.MECA))
                .thenReturn(List.of(e));

        TeacherDTO prof = new TeacherDTO();
        prof.setFirstname("Ali"); prof.setLastname("Benali");

        when(teacherClient.getTeacherById(5L)).thenReturn(prof);

        var res = service.getMatieresEtProfs("CI2", Filiere.MECA);

        assertEquals(1, res.size());
        assertEquals("Communication mobile", res.get(0).getMatiere());
        assertEquals("Ali", res.get(0).getProfPrenom());
    }


    @Test
    void getStudentsStatusPerClass_shouldReturnActiveAndInactiveCounts() {
        Classe c = new Classe(); c.setNom("CI2"); c.setFiliere(Filiere.MECA);

        when(edtRepo.findDistinctClassesByProfId(1L))
                .thenReturn(List.of(c));

        when(teacherClient.countStudentsByClass("CI2", "MECA", true))
                .thenReturn(Map.of("count", 20));

        when(teacherClient.countStudentsByClass("CI2", "MECA", false))
                .thenReturn(Map.of("count", 5));

        var res = service.getStudentsStatusPerClass(1L);

        assertEquals(1, res.size());
        assertEquals(20, res.get(0).get("activeStudents"));
        assertEquals(5, res.get(0).get("inactiveStudents"));
    }

    @Test
    void deleteEmploisByGroup_shouldDeleteMatchingEmplois() {
        Classe c = new Classe(); c.setNom("CI2"); c.setFiliere(Filiere.MECA);

        EmploiDuTemps e = new EmploiDuTemps();
        e.setClasse(c); e.setSemestre(Semestre.S1);

        when(edtRepo.findAll()).thenReturn(List.of(e));

        service.deleteEmploisByGroup("CI2", "MECA", "S1");

        verify(edtRepo).delete(e);
    }


    @Test
    void updateEmploiCell_shouldUpdateMatiereAndSalle() {
        EmploiDuTemps e = new EmploiDuTemps(); e.setId(1L);

        when(edtRepo.findById(1L)).thenReturn(Optional.of(e));

        Matiere m = new Matiere(); m.setNom("Les smartphones");
        when(matiereRepo.findByNom("Les smartphones")).thenReturn(Optional.of(m));

        Salle s = new Salle(); s.setNom("S2");
        when(salleRepo.findByNom("S2")).thenReturn(Optional.of(s));

        EmploiCellUpdateDTO dto = new EmploiCellUpdateDTO();
        dto.setMatiere("Les smartphones");
        dto.setSalle("S2");

        service.updateEmploiCell(1L, dto);

        assertEquals("Les smartphones", e.getMatiere().getNom());
        assertEquals("S2", e.getSalle().getNom());
    }



    @Test
    void getMyClassesCount_shouldReturnNumberOfDistinctClasses() {
        Classe c1 = new Classe(); c1.setNom("CI2");
        Classe c2 = new Classe(); c2.setNom("CI1");

        when(edtRepo.findDistinctClassesByProfId(1L))
                .thenReturn(List.of(c1, c2));

        int count = service.getMyClassesCount(1L);

        assertEquals(2, count);
    }

    @Test
    void getEmploiByClasse_shouldReturnDTOList() {
        Classe c = new Classe(); c.setId(1L); c.setNom("CI2");
        Matiere m = new Matiere(); m.setId(1L); m.setNom("Communication mobile");
        Salle s = new Salle(); s.setId(1L); s.setNom("S6");
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

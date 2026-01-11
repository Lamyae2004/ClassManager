package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClasseServiceTest {

    @Mock
    private ClasseRepository classeRepo;

    @InjectMocks
    private ClasseService service;

    @Test
    void getClassesByTeacherId_shouldReturnDTOs() {
        Classe c = new Classe();
        c.setId(1L); c.setNom("CI2"); c.setFiliere(Filiere.MECA);

        when(classeRepo.findByProfId(5L)).thenReturn(List.of(c));

        var res = service.getClassesByTeacherId(5L);

        assertEquals(1, res.size());
        assertEquals("CI2", res.get(0).getNom());
    }

    @Test
    void getClassById_shouldReturnDTO() {
        Classe c = new Classe();
        c.setId(1L); c.setNom("CI1"); c.setFiliere(Filiere.MECA);

        when(classeRepo.findById(1L)).thenReturn(Optional.of(c));

        var res = service.getClassById(1L);

        assertEquals("CI1", res.getNom());
    }
}

package com.class_manager.Gestion_des_emplois.service;


import com.class_manager.Gestion_des_emplois.model.dto.ClasseDTO;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClasseService {
    private final ClasseRepository classeRepository;

    public List<ClasseDTO> getClassesByProf(Long profId) {
        return classeRepository.findByProfId(profId) // méthode à créer dans repository
                .stream()
                .map(c -> new ClasseDTO(
                        c.getId(),
                        c.getNom(),
                        c.getFiliere().getNom()
                ))
                .toList();
    }
}

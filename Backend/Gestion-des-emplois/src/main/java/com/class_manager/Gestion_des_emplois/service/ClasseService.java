package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.model.dto.ClasseDTO;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClasseService {
    private final ClasseRepository classeRepository;

    // Méthode pour récupérer les classes d'un prof à partir de son ID
    public List<ClasseDTO> getClassesByTeacherId(Long teacherId) {
        return classeRepository.findByProfId(teacherId)
                .stream()
                .map(classe -> new ClasseDTO(
                        classe.getId(),
                        classe.getNom(),
                        classe.getFiliere() != null ? classe.getFiliere().name() : null

                ))
                .toList();
    }

    public ClasseDTO getClassByNiveauAndFiliere(String niveau, String filiere) {
        Classe classe = classeRepository.findByNomAndFiliere(niveau, Filiere.valueOf(filiere))
                .orElseThrow(() -> new IllegalArgumentException("Aucune classe trouvée pour ce niveau et cette filière"));
        return new ClasseDTO(
                classe.getId(),
                classe.getNom(),
                classe.getFiliere().name()
        );
    }

    public ClasseDTO getClassById(Long id) {
            Classe classe = classeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Classe introuvable"));
            return new ClasseDTO(
                    classe.getId(),
                    classe.getNom(),
                    classe.getFiliere().name()
            );
        }
}

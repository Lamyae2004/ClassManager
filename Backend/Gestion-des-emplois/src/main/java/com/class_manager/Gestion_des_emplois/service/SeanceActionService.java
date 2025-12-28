package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.EtudiantDTO;
import com.class_manager.Gestion_des_emplois.model.dto.SeanceActionRequest;
import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import com.class_manager.Gestion_des_emplois.model.entity.SeanceAction;
import com.class_manager.Gestion_des_emplois.repository.EmploiDuTempsRepository;
import com.class_manager.Gestion_des_emplois.repository.SeanceActionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceActionService {

    private final SeanceActionRepository actionRepo;
    private final EmploiDuTempsRepository emploiRepo;
    private final TeacherClient teacherClient;
    private final EmailNotificationService emailService;

    @Transactional
    public SeanceAction declareAction(SeanceActionRequest request) {
        // 1. Récupérer l'emploi du temps
        EmploiDuTemps emploi = emploiRepo.findById(request.getEmploiId())
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        // 2. Créer l'action
        SeanceAction action = new SeanceAction();
        action.setEmploiId(request.getEmploiId());
        action.setProfId(emploi.getProfId());
        action.setAction(
                "ANNULER".equalsIgnoreCase(request.getAction()) ?
                        SeanceAction.ActionType.ANNULER :
                        SeanceAction.ActionType.RETARD
        );
        action.setMotif(request.getMotif());
        action.setDateAction(LocalDateTime.now());
        action.setEmailEnvoye(false);

        SeanceAction savedAction = actionRepo.save(action);

        // 3. Récupérer les étudiants de la classe
        String niveau = emploi.getClasse().getNom();
        String filiere = emploi.getClasse().getFiliere() != null ?
                emploi.getClasse().getFiliere().name() : "NONE";

        // 4. Récupérer les infos du prof
        TeacherDTO prof = teacherClient.getTeacherById(emploi.getProfId());

        // 5. Envoyer les emails
        try {
            emailService.envoyerNotificationEtudiants(
                    emploi,
                    prof,
                    action.getAction().name(),
                    action.getMotif(),
                    niveau,
                    filiere
            );

            savedAction.setEmailEnvoye(true);
            actionRepo.save(savedAction);

        } catch (Exception e) {
            e.printStackTrace();
            // Email échoué mais action enregistrée
        }

        return savedAction;
    }

    public List<SeanceAction> getActionsByEmploi(Long emploiId) {
        return actionRepo.findByEmploiId(emploiId);
    }

    public List<SeanceAction> getActionsByProf(Long profId) {
        return actionRepo.findByProfId(profId);
    }
}
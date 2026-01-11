package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.dto.SeanceActionRequest;
import com.class_manager.Gestion_des_emplois.model.entity.SeanceAction;
import com.class_manager.Gestion_des_emplois.service.SeanceActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emploi/actions")
@RequiredArgsConstructor
public class SeanceActionController {

    private final SeanceActionService actionService;

    /**
     * Déclarer une annulation ou un retard
     */
    @PostMapping("/declare")
    public ResponseEntity<Map<String, Object>> declareAction(
            @RequestBody SeanceActionRequest request) {

        try {
            SeanceAction action = actionService.declareAction(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", action.getAction() == SeanceAction.ActionType.ANNULER ?
                    "Séance annulée avec succès" :
                    "Retard déclaré avec succès");
            response.put("action", action);
            response.put("emailEnvoye", action.getEmailEnvoye());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer l'historique des actions pour une séance
     */
    @GetMapping("/emploi/{emploiId}")
    public ResponseEntity<List<SeanceAction>> getActionsByEmploi(
            @PathVariable Long emploiId) {
        return ResponseEntity.ok(actionService.getActionsByEmploi(emploiId));
    }

    /**
     * Récupérer l'historique des actions d'un professeur
     */
    @GetMapping("/prof/{profId}")
    public ResponseEntity<List<SeanceAction>> getActionsByProf(
            @PathVariable Long profId) {
        return ResponseEntity.ok(actionService.getActionsByProf(profId));
    }
}
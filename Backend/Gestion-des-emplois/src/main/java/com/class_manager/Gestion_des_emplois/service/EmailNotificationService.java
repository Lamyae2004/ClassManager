package com.class_manager.Gestion_des_emplois.service;

import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.EtudiantActionDTO;
import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;  // ✅ UNE SEULE DÉCLARATION
    private final TeacherClient teacherClient;
    private final JavaMailSender javaMailSender ;

    public void envoyerNotificationEtudiants(
            EmploiDuTemps emploi,
            TeacherDTO prof,
            String action,
            String motif,
            String niveau,
            String filiere) {

        try {
            // ✅ RÉCUPÉRER LA LISTE DES ÉTUDIANTS VIA FEIGN CLIENT
            List<EtudiantActionDTO> etudiants = teacherClient.getStudentsByClasse(filiere, niveau);

            // Log de l'action
            System.out.println("=== NOTIFICATION EMAIL ===");
            System.out.println("Action: " + action);
            System.out.println("Classe: " + niveau + " - " + filiere);
            System.out.println("Matière: " + emploi.getMatiere().getNom());
            System.out.println("Prof: " + prof.getFirstname() + " " + prof.getLastname());
            System.out.println("Créneau: " + emploi.getCreneau().getHeureDebut() +
                    " - " + emploi.getCreneau().getHeureFin());
            System.out.println("Motif: " + motif);
            System.out.println("Nombre d'étudiants: " + etudiants.size());
            // 🔹 Afficher tous les emails récupérés
            for (EtudiantActionDTO etudiant : etudiants) {
                System.out.println("Email étudiant: " + etudiant.getEmail());
            }


            // ✅ ENVOYER UN EMAIL À CHAQUE ÉTUDIANT
            for (EtudiantActionDTO etudiant : etudiants) {
                envoyerEmail(
                        etudiant.getEmail(),
                        action,
                        emploi,
                        prof,
                        motif
                );
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi des emails: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible d'envoyer les notifications", e);
        }
    }

    private void envoyerEmail(
            String destinataire,
            String action,
            EmploiDuTemps emploi,
            TeacherDTO prof,
            String motif) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nourelhouda.essafi@uit.ac.ma");
        message.setTo(destinataire);

        String sujet;
        String contenu;

        if ("ANNULER".equalsIgnoreCase(action)) {
            sujet = "🚫 Cours annulé - " + emploi.getMatiere().getNom();
            contenu = String.format(
                    "Bonjour,\n\n" +
                            "Nous vous informons que le cours suivant a été ANNULÉ :\n\n" +
                            "Matière : %s\n" +
                            "Professeur : %s %s\n" +
                            "Jour : %s\n" +
                            "Horaire : %s - %s\n" +
                            "Salle : %s\n\n" +
                            "%s" +
                            "\nCordialement,\n" +
                            "L'administration",
                    emploi.getMatiere().getNom(),
                    prof.getFirstname(),
                    prof.getLastname(),
                    emploi.getJour(),
                    emploi.getCreneau().getHeureDebut(),
                    emploi.getCreneau().getHeureFin(),
                    emploi.getSalle().getNom(),
                    motif != null ? "Motif : " + motif + "\n" : ""
            );
        } else {
            sujet = "⏰ Retard signalé - " + emploi.getMatiere().getNom();
            contenu = String.format(
                    "Bonjour,\n\n" +
                            "Votre professeur a signalé un RETARD pour le cours suivant :\n\n" +
                            " Matière : %s\n" +
                            " Professeur : %s %s\n" +
                            "Jour : %s\n" +
                            "Horaire : %s - %s\n" +
                            "Salle : %s\n\n" +
                            "%s" +
                            "\nMerci de votre compréhension.\n\n" +
                            "Cordialement,\n" +
                            "L'administration",
                    emploi.getMatiere().getNom(),
                    prof.getFirstname(),
                    prof.getLastname(),
                    emploi.getJour(),
                    emploi.getCreneau().getHeureDebut(),
                    emploi.getCreneau().getHeureFin(),
                    emploi.getSalle().getNom(),
                    motif != null ? "Motif : " + motif + "\n" : ""
            );
        }

        message.setSubject(sujet);
        message.setText(contenu);

        try {
            javaMailSender.send(message);  // ✅ Utiliser mailSender
            System.out.println("✅ Email envoyé à : " + destinataire);
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email à " + destinataire + ": " + e.getMessage());
        }
    }
}
package com.class_manager.Gestion_des_absences.service;

import com.class_manager.Gestion_des_absences.client.EmploiDuTempsClient;
import com.class_manager.Gestion_des_absences.model.dto.AbsenceResponseDTO;
import com.class_manager.Gestion_des_absences.model.entity.Absence;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.AbsenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbsenceService {
    private final AbsenceRepository absenceRepository;
    private final EmploiDuTempsClient emploiDuTempsClient;
    private static final String UPLOAD_DIR = "uploads/justifications/";

    public List<AbsenceResponseDTO> getAbsencesByEtudiantId(Long etudiantId) {
        List<Absence> absences = absenceRepository.findByEtudiantId(etudiantId);
        return absences.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AbsenceResponseDTO uploadJustification(Long absenceId, MultipartFile file) throws IOException {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée"));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "absence_" + absenceId + "_" + System.currentTimeMillis() + extension;

        // Save file
        Path targetPath = uploadPath.resolve(filename).normalize();
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Update absence
        absence.setFilePath(targetPath.toString());
        absence.setFileName(originalFilename);
        absence.setJustifie(false); // Set to false initially, admin will validate later

        absenceRepository.save(absence);

        return toDTO(absence);
    }

    public AbsenceResponseDTO updateJustificationStatus(Long absenceId, boolean justifie) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée"));
        
        absence.setJustifie(justifie);
        
        // If rejecting, clear the file path and file name so student can upload a new justification
        if (!justifie && absence.getFilePath() != null) {
            try {
                // Optionally delete the physical file
                Path filePath = Paths.get(absence.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                // Log error but continue - file might already be deleted
                System.err.println("Error deleting file: " + e.getMessage());
            }
            // Clear file references
            absence.setFilePath(null);
            absence.setFileName(null);
        }
        
        absenceRepository.save(absence);
        
        return toDTO(absence);
    }

    private AbsenceResponseDTO toDTO(Absence absence) {
        AbsenceResponseDTO dto = new AbsenceResponseDTO();
        dto.setId(absence.getId());
        dto.setEtudiantId(absence.getEtudiantId());
        dto.setPresent(absence.isPresent());
        dto.setJustifie(absence.isJustifie());
        dto.setFilePath(absence.getFilePath());
        dto.setFileName(absence.getFileName());

        if (absence.getSeance() != null) {
            Seance seance = absence.getSeance();
            dto.setSeanceId(seance.getId());
            dto.setDate(seance.getDate());
            dto.setMatiereId(seance.getMatiereId());
            dto.setCreneauId(seance.getCreneauId());

            // Fetch matiere name
            if (seance.getMatiereId() != null) {
                try {
                    Map<String, Object> matiere = emploiDuTempsClient.getMatiereById(seance.getMatiereId());
                    if (matiere != null && matiere.containsKey("nom")) {
                        dto.setMatiereNom((String) matiere.get("nom"));
                    }
                } catch (Exception e) {
                    // Log error but don't fail
                    System.err.println("Error fetching matiere: " + e.getMessage());
                }
            }

            // Fetch creneau times
            if (seance.getCreneauId() != null) {
                try {
                    Map<String, Object> creneau = emploiDuTempsClient.getCreneauById(seance.getCreneauId());
                    if (creneau != null) {
                        if (creneau.containsKey("heureDebut")) {
                            dto.setHeureDebut((String) creneau.get("heureDebut"));
                        }
                        if (creneau.containsKey("heureFin")) {
                            dto.setHeureFin((String) creneau.get("heureFin"));
                        }
                    }
                } catch (Exception e) {
                    // Log error but don't fail
                    System.err.println("Error fetching creneau: " + e.getMessage());
                }
            }
        }

        return dto;
    }
}

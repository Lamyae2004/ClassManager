package com.class_manager.Gestion_des_absences.controller;

import com.class_manager.Gestion_des_absences.model.dto.ClassAbsenceRateDTO;
import com.class_manager.Gestion_des_absences.model.dto.SeanceDTO;
import com.class_manager.Gestion_des_absences.model.dto.StudentsStatusByClassDTO;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/absences")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AbsenceController {
    private final SeanceService seanceService;


    @GetMapping("/dépassertaux/{profId}")
    public List<StudentsStatusByClassDTO> getStudentsStatus(
            @PathVariable Long profId
    ) {
        return seanceService.getStudentsStatusByClassForProf(profId, 0.25);
    }


    @GetMapping("/classes-by-absence")
    public List<ClassAbsenceRateDTO> classesByAbsence() {
        return seanceService.getAbsenceRateByClassAndFiliere();
    }


    @GetMapping("/classes/{classeId}/user/{userId}")
    public ResponseEntity<List<Seance>> getSeancesForUser(
            @PathVariable Long classeId,
            @PathVariable Long userId) {
        List<Seance> seances = seanceService.getSeancesByClasseAndUser(classeId, userId);
        return ResponseEntity.ok(seances);
    }


    @PostMapping
    public ResponseEntity<Seance> saveAbsences(@RequestBody SeanceDTO request) {
        Seance seance = seanceService.saveSeanceWithAbsences(request);
        return ResponseEntity.ok(seance);
    }
}

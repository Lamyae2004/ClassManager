package com.class_manager.Gestion_des_absences.service;


import com.class_manager.Gestion_des_absences.client.EtudiantClient;
import com.class_manager.Gestion_des_absences.model.dto.*;
import com.class_manager.Gestion_des_absences.model.entity.Absence;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.AbsenceRepository;
import com.class_manager.Gestion_des_absences.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeanceService {
    private final SeanceRepository seanceRepository;
    private final EtudiantClient etudiantClient;
    private final AbsenceRepository absenceRepository;

    public List<StudentsStatusByClassDTO> getStudentsStatusByClassForProf(
            Long profId,
            double absenceThreshold // ex: 0.25
    ) {

        // 1️⃣ Tous les étudiants
        List<StudentDTO> students = etudiantClient.getAllStudents();
        Map<Long, StudentDTO> studentMap = students.stream()
                .collect(Collectors.toMap(StudentDTO::getId, s -> s));

        // Absences - CORRECTION ICI
        Map<Long, Long> absencesMap = absenceRepository
                .countAbsencesByStudentForProf(profId)
                .stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).longValue(),  // Cast vers Number puis Long
                        r -> ((Number) r[1]).longValue()
                ));

        //  Total séances - CORRECTION ICI
        Map<Long, Long> totalMap = absenceRepository
                .countTotalSeancesByStudentForProf(profId)
                .stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).longValue(),  // Cast vers Number puis Long
                        r -> ((Number) r[1]).longValue()
                ));

        // Regroupement par classe + filière
        Map<String, StudentsStatusByClassDTO> result = new HashMap<>();

        for (Long studentId : totalMap.keySet()) {

            StudentDTO student = studentMap.get(studentId);
            if (student == null) continue;

            long absences = absencesMap.getOrDefault(studentId, 0L);
            long total = totalMap.get(studentId);

            double rate = (double) absences / total;

            String key = student.getNiveau() + "-" + student.getFiliere();

            result.putIfAbsent(key,
                    new StudentsStatusByClassDTO(
                            student.getNiveau(),
                            student.getFiliere(),
                            0,
                            0
                    )
            );

            StudentsStatusByClassDTO dto = result.get(key);

            if (rate > absenceThreshold) {
                dto.setInactiveStudents(dto.getInactiveStudents() + 1);
            } else {
                dto.setActiveStudents(dto.getActiveStudents() + 1);
            }
        }

        return new ArrayList<>(result.values());
    }

    public List<ClassAbsenceRateDTO> getAbsenceRateByClassAndFiliere() {

        List<StudentDTO> students = etudiantClient.getAllStudents();

        Map<Long, StudentDTO> studentMap = students.stream()
                .collect(Collectors.toMap(StudentDTO::getId, s -> s));

        // CORRECTION ICI
        List<Object[]> absences = absenceRepository.countAbsencesByStudent();

        Map<String, Long> absenceCountByClass = new HashMap<>();
        Map<String, Long> studentCountByClass = new HashMap<>();

        for (Object[] row : absences) {
            Long studentId = ((Number) row[0]).longValue();  // Cast correct
            Long count = ((Number) row[1]).longValue();      // Cast correct

            StudentDTO student = studentMap.get(studentId);
            if (student == null) continue;

            String key = student.getNiveau() + "-" + student.getFiliere();

            absenceCountByClass.merge(key, count, Long::sum);
            studentCountByClass.merge(key, 1L, Long::sum);
        }

        List<ClassAbsenceRateDTO> result = new ArrayList<>();

        for (String key : absenceCountByClass.keySet()) {
            double rate = (absenceCountByClass.get(key) * 100.0)
                    / studentCountByClass.get(key);

            String[] parts = key.split("-");
            result.add(new ClassAbsenceRateDTO(
                    parts[0],
                    parts[1],
                    Math.round(rate * 100.0) / 100.0
            ));
        }

        return result;
    }

    public List<Seance> getSeancesByClasseAndUser(Long classeId, Long userId) {

        UserDTO user = etudiantClient.getUserById(userId);

        if (user == null || user.getRole() == null) {
            throw new RuntimeException("Utilisateur ou rôle invalide");
        }

        return switch (user.getRole()) {
            case ADMIN -> seanceRepository.findByClasseId(classeId);
            case TEACHER -> seanceRepository.findByClasseIdAndProfId(classeId, userId);
            default -> List.of();
        };
    }

    public Seance saveSeanceWithAbsences(SeanceDTO request) {
        Seance seance = new Seance();
        seance.setProfId(request.getProfId());
        seance.setClasseId(request.getClasseId());
        seance.setCreneauId(request.getCreneauId());
        seance.setDate(LocalDate.parse(request.getDate()));
        seance.setMatiereId(request.getMatiereId());
        seance.setSalleId(request.getSalleId());

        List<Absence> absences = request.getAbsences().stream().map(a -> {
            Absence absence = new Absence();
            absence.setEtudiantId(a.getEtudiantId());
            absence.setPresent(a.isPresent());
            absence.setSeance(seance);
            return absence;
        }).toList();

        seance.setAbsences(absences);

        return seanceRepository.save(seance);
    }
}
package com.ensa.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ensa.mobile.utils.Absence;

import java.util.ArrayList;
import java.util.List;

public class FragmentAbsenceEtudiant extends Fragment {

    Spinner spinnerMatiere;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_absence_etudiant, container, false);

        spinnerMatiere = view.findViewById(R.id.spinnerMatiere);
        recyclerView = view.findViewById(R.id.recyclerAbsences);

        // Matières statiques
        List<String> matieres = new ArrayList<>();
        matieres.add("Toutes");
        matieres.add("Analyse");
        matieres.add("Java");
        matieres.add("Algo");

        spinnerMatiere.setAdapter(
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        matieres)
        );

        // Absences statiques
// Absences statiques
        List<Absence> absences = new ArrayList<>();

        absences.add(new Absence(
                "Analyse",
                "02/12/2025",
                "14:00 - 16:00",
                "NON_JUSTIFIEE"
        ));

        absences.add(new Absence(
                "Java",
                "05/12/2025",
                "10:00 - 12:00",
                "VALIDEE"
        ));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AbsenceAdapter(absences));

        return view;
    }
}

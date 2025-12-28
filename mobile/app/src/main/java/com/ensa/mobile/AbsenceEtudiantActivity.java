package com.ensa.mobile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ensa.mobile.utils.Absence;
import java.util.ArrayList;
import java.util.List;

public class AbsenceEtudiantActivity extends AppCompatActivity {

    Spinner spinnerMatiere;
    RecyclerView recyclerAbsences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_etudiant);

        spinnerMatiere = findViewById(R.id.spinnerMatiere);
        recyclerAbsences = findViewById(R.id.recyclerAbsences);

        // Données statiques
        List<String> matieres = new ArrayList<>();
        matieres.add("Toutes");
        matieres.add("Analyse");
        matieres.add("Algorithme");
        matieres.add("Java");

        spinnerMatiere.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        matieres)
        );

        List<Absence> absences = new ArrayList<>();
        absences.add(new Absence("Analyse", "02/12/2025", "14:00 - 16:00", "NON_JUSTIFIEE"));
        absences.add(new Absence("Java", "05/12/2025", "10:00 - 12:00", "VALIDEE"));

        recyclerAbsences.setLayoutManager(new LinearLayoutManager(this));
        recyclerAbsences.setAdapter(new AbsenceAdapter(absences));
    }
}

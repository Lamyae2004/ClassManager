package com.ensa.mobile.absence.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.mobile.R;
import com.ensa.mobile.absence.adapters.AbsenceAdapter;
import com.ensa.mobile.absence.api.AbsenceRetrofitClient;
import com.ensa.mobile.absence.models.AbsenceResponse;
import com.ensa.mobile.authentification.api.RetrofitClient;
import com.ensa.mobile.authentification.models.UserResponse;
import com.ensa.mobile.absence.models.Absence;
import com.ensa.mobile.utils.TokenManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAbsenceEtudiant extends Fragment {

    private static final String TAG = "FragmentAbsence";

    Spinner spinnerMatiere;
    RecyclerView recyclerView;
    private List<Absence> allAbsences = new ArrayList<>();
    private AbsenceAdapter adapter;
    private TokenManager tokenManager;

    // ✅ ActivityResultLauncher pour gérer les résultats de fichiers
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Initialiser les launchers AVANT onCreateView
        initializeActivityResultLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_absence_etudiant, container, false);

        tokenManager = TokenManager.getInstance(requireContext());
        spinnerMatiere = view.findViewById(R.id.spinnerMatiere);
        recyclerView = view.findViewById(R.id.recyclerAbsences);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AbsenceAdapter(new ArrayList<>(), requireActivity(), this);
        recyclerView.setAdapter(adapter);

        // Load user profile to get user ID
        loadUserProfileAndAbsences();

        // Filter by matiere
        spinnerMatiere.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterAbsencesByMatiere((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        return view;
    }

    //  Initialiser les ActivityResultLaunchers
    private void initializeActivityResultLaunchers() {
        // File Picker (PDF)
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "File picker result received");
                    if (adapter != null) {
                        Intent data = new Intent();
                        if (result.getData() != null) {
                            data.setData(result.getData().getData());
                        }
                        adapter.handleActivityResult(1001, result.getResultCode(), data);
                    }
                }
        );

        // Camera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Camera result received");
                    if (adapter != null) {
                        adapter.handleActivityResult(1002, result.getResultCode(), result.getData());
                    }
                }
        );

        // Gallery
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Gallery result received");
                    if (adapter != null) {
                        Intent data = new Intent();
                        if (result.getData() != null) {
                            data.setData(result.getData().getData());
                        }
                        adapter.handleActivityResult(1003, result.getResultCode(), data);
                    }
                }
        );
    }

    // Méthodes publiques pour lancer les intents depuis l'Adapter
    public void launchFilePicker(Intent intent) {
        Log.d(TAG, "Launching file picker");
        filePickerLauncher.launch(intent);
    }

    public void launchCamera(Intent intent) {
        Log.d(TAG, "Launching camera");
        cameraLauncher.launch(intent);
    }

    public void launchGallery(Intent intent) {
        Log.d(TAG, "Launching gallery");
        galleryLauncher.launch(intent);
    }

    private void loadUserProfileAndAbsences() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserResponse> profileCall = RetrofitClient.getInstance().getAuthApiService().getProfile("Bearer " + token);
        profileCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long userId = response.body().getId();
                    loadAbsences(userId);
                } else {
                    Toast.makeText(requireContext(), "Erreur lors du chargement du profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAbsences(Long etudiantId) {
        Call<List<AbsenceResponse>> call = AbsenceRetrofitClient.getInstance()
                .getAbsenceApiService()
                .getAbsencesByStudent(etudiantId);

        call.enqueue(new Callback<List<AbsenceResponse>>() {
            @Override
            public void onResponse(Call<List<AbsenceResponse>> call, Response<List<AbsenceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allAbsences = convertToAbsenceList(response.body());

                    // Update matiere spinner
                    updateMatiereSpinner();

                    // Show all absences initially
                    adapter.updateAbsences(allAbsences);
                } else {
                    Toast.makeText(requireContext(), "Aucune absence trouvée", Toast.LENGTH_SHORT).show();
                    adapter.updateAbsences(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<AbsenceResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur lors du chargement des absences: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Absence> convertToAbsenceList(List<AbsenceResponse> responses) {
        List<Absence> absences = new ArrayList<>();
        for (AbsenceResponse response : responses) {
            if (!response.isPresent()) {
                String matiere = response.getMatiereNom() != null && !response.getMatiereNom().isEmpty()
                        ? response.getMatiereNom() : "Matière";
                String date = response.getFormattedDate();
                String heure = response.getTimeRange();
                if (heure == null || heure.isEmpty()) {
                    if (response.getHeureDebut() != null && response.getHeureFin() != null) {
                        heure = response.getHeureDebut() + " - " + response.getHeureFin();
                    }
                }
                String etat = response.getStatusString();
                Long id = response.getId();
                String fileName = response.getFileName();

                absences.add(new Absence(matiere, date, heure, etat, id, fileName));
            }
        }
        return absences;
    }

    private void updateMatiereSpinner() {
        Set<String> matieresSet = new HashSet<>();
        matieresSet.add("Toutes");
        for (Absence absence : allAbsences) {
            if (absence.matiere != null && !absence.matiere.isEmpty()) {
                matieresSet.add(absence.matiere);
            }
        }

        List<String> matieres = new ArrayList<>(matieresSet);
        spinnerMatiere.setAdapter(
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        matieres)
        );
    }

    private void filterAbsencesByMatiere(String selectedMatiere) {
        if ("Toutes".equals(selectedMatiere)) {
            adapter.updateAbsences(allAbsences);
        } else {
            List<Absence> filtered = new ArrayList<>();
            for (Absence absence : allAbsences) {
                if (selectedMatiere.equals(absence.matiere)) {
                    filtered.add(absence);
                }
            }
            adapter.updateAbsences(filtered);
        }
    }
}
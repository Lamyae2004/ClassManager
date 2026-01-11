package com.ensa.mobile.gestionDocuments.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.mobile.R;
import com.ensa.mobile.gestionDocuments.adapters.DocumentAdapter;
import com.ensa.mobile.gestionDocuments.api.ClasseService;
import com.ensa.mobile.gestionDocuments.api.DocumentService;
import com.ensa.mobile.gestionDocuments.api.EmploiService;
import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.ClasseEtudiantDto;
import com.ensa.mobile.gestionDocuments.models.Document;
import com.ensa.mobile.gestionDocuments.models.DocumentDto;
import com.ensa.mobile.gestionDocuments.models.MatiereDto;
import com.ensa.mobile.gestionDocuments.models.MatiereProfDto;
import com.ensa.mobile.utils.TokenManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDocumentsEtudiants extends Fragment {

    private RecyclerView recyclerView;
    private Spinner spinnerMatiere, spinnerType;
    private DocumentAdapter adapter;
    private List<Document> allDocuments = new ArrayList<>();
    private List<MatiereDto> matieres = new ArrayList<>();
    private List<String> types = Arrays.asList("COURS", "TP", "TD");
    private ClasseEtudiantDto etudiantClasse;

    // Map matiereId -> MatiereProfDto pour récupérer le prof
    private Map<Long, MatiereProfDto> matiereIdMap = new HashMap<>();

    public FragmentDocumentsEtudiants() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents_etudiant, container, false);

        recyclerView = view.findViewById(R.id.recycler_documents);
        spinnerMatiere = view.findViewById(R.id.spinner_matiere);
        spinnerType = view.findViewById(R.id.spinner_type);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DocumentAdapter(new ArrayList<>(), getContext(), matiereIdMap);
        recyclerView.setAdapter(adapter);

        loadClasseEtudiant();

        return view;
    }

    private void loadClasseEtudiant() {
        Long studentId = TokenManager.getInstance(getContext()).getStudentId();
        ClasseService userClasseService = new ClasseService();
        ClasseService classeService2 = new ClasseService();

        // 1️⃣ Premier appel : récupérer niveau + filiere de l'étudiant
        userClasseService.getClasse(studentId, new Callback<ClasseEtudiantDto>() {
            @Override
            public void onResponse(Call<ClasseEtudiantDto> call, Response<ClasseEtudiantDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etudiantClasse = response.body();

                    // 2️⃣ Deuxième appel : récupérer classeId à partir de niveau + filiere
                    classeService2.getClasseId(etudiantClasse.getNiveau(), etudiantClasse.getFiliere(),
                            new Callback<ClasseDto>() {
                                @Override
                                public void onResponse(Call<ClasseDto> call, Response<ClasseDto> response2) {
                                    if (response2.isSuccessful() && response2.body() != null) {
                                        etudiantClasse.setClasseId(response2.body().getId()); // mettre le setter dans ClasseEtudiantDto
                                        loadMatieres(); // continuer le reste
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Classe introuvable pour ce niveau/filière",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ClasseDto> call, Throwable t) {
                                    Toast.makeText(getContext(),
                                            "Erreur récupération id de la classe",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(getContext(),
                            "Impossible de récupérer les informations de l'étudiant",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClasseEtudiantDto> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Erreur récupération classe de l'étudiant",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadDocumentsFromApi(Long moduleId, String type) {

        DocumentService service = new DocumentService();

        service.getDocuments(
                etudiantClasse.getClasseId(),
                moduleId,
                type,
                new Callback<List<Document>>() {

                    @Override
                    public void onResponse(Call<List<Document>> call,
                                           Response<List<Document>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            Toast.makeText(getContext(),
                                    "URL params: classeId=" + etudiantClasse.getClasseId()
                                            + " moduleId=" + moduleId
                                            + " type=" + type,
                                    Toast.LENGTH_LONG).show();

                            allDocuments.clear();

                            for (Document d : response.body()) {
                                String fileUrl = "http://10.0.2.2:8080/api/document/" + d.getFileUrl();
                                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // bc6c390a-2ede-4ae1-8d4e-63ff12db9030.docx

                                allDocuments.add(new Document(
                                        d.getTitle(),
                                        d.getType(),
                                        d.getModuleId(),
                                        d.getProfId(),
                                        d.getClasseId(),
                                        fileUrl,
                                        fileName
                                      //  "http://10.0.2.2:8080/" + d.getFileUrl()   // IMPORTANT
                                ));
                            }

                            filterDocuments();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Document>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Erreur chargement documents", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMatieres() {
        EmploiService emploiService = new EmploiService();

        emploiService.getMatieresEtProfs(etudiantClasse.getNiveau(), etudiantClasse.getFiliere(),
                new Callback<List<MatiereProfDto>>() {
                    @Override
                    public void onResponse(Call<List<MatiereProfDto>> call, Response<List<MatiereProfDto>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            matieres.clear();
                            allDocuments.clear();
                            matiereIdMap.clear();

                            int i = 0;
                            for(MatiereProfDto mp : response.body()) {
                                // Stocker matiereId -> MatiereProfDto
                                matiereIdMap.put(mp.getMatiereId(), mp);

                                // Ajouter la matière pour le spinner
                                matieres.add(new MatiereDto(mp.getMatiereId(), mp.getMatiere()));

                                // Créer un document statique
                                String type = types.get(i % types.size());
                                i++;
                            }

                            setupSpinners();
                            filterDocuments();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MatiereProfDto>> call, Throwable t) {
                        Toast.makeText(getContext(), "Erreur récupération matières", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSpinners() {
        ArrayAdapter<MatiereDto> matiereAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, matieres);
        matiereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatiere.setAdapter(matiereAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MatiereDto selectedMatiere = (MatiereDto) spinnerMatiere.getSelectedItem();
                String selectedType = (String) spinnerType.getSelectedItem();

                if (selectedMatiere != null && selectedType != null && etudiantClasse != null) {
                    loadDocumentsFromApi(selectedMatiere.getId(), selectedType);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerMatiere.setOnItemSelectedListener(listener);
        spinnerType.setOnItemSelectedListener(listener);
    }

    private void filterDocuments() {
        MatiereDto selectedMatiere = (MatiereDto) spinnerMatiere.getSelectedItem();
        String selectedType = (String) spinnerType.getSelectedItem();

        List<Document> filtered = new ArrayList<>();
        for(Document doc : allDocuments) {
            boolean matchMatiere = selectedMatiere == null || doc.getModuleId().equals(selectedMatiere.getId());
            boolean matchType = selectedType == null || doc.getType().equalsIgnoreCase(selectedType);

            if(matchMatiere && matchType) {
                filtered.add(doc);
            }
        }

        adapter.updateList(filtered);
    }
}

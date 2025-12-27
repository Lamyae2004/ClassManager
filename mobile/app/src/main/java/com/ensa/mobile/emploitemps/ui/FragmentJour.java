package com.ensa.mobile.emploitemps.ui;


import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ensa.mobile.R;
import com.ensa.mobile.emploitemps.adapters.EmploiAdapter;
import com.ensa.mobile.emploitemps.api.EmploiRetrofitClient;
import com.ensa.mobile.emploitemps.models.EmploiDuTempsDTO;
import com.ensa.mobile.emploitemps.models.EmploiEtudiantResponse;
import com.ensa.mobile.emploitemps.models.EmploiWrapperDTO;
import com.ensa.mobile.utils.TokenManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentJour extends Fragment {

    private static final String TAG = "FragmentJour";
    private static final String ARG_JOUR = "jour";


    private String jour;
    private RecyclerView recyclerView;
    private EmploiAdapter adapter;
    private List<EmploiDuTempsDTO> data = new ArrayList<>();

    public static FragmentJour newInstance(String jour) {
        FragmentJour fragment = new FragmentJour();
        Bundle args = new Bundle();
        args.putString(ARG_JOUR, jour);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jour = getArguments().getString(ARG_JOUR);
            Log.d(TAG, "Fragment créé pour le jour: " + jour);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView appelé");
        return inflater.inflate(R.layout.fragment_jour, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated - initialisation RecyclerView");

        recyclerView = view.findViewById(R.id.recyclerViewJour);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmploiAdapter(data);
        recyclerView.setAdapter(adapter);

        loadEmploi();
    }
    public static String calculerEtat(String jour, String debut, String fin) {
        // Comparer le jour
        Calendar c = Calendar.getInstance();
        String[] jours = {"Dimanche","Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi"};
        String jourActuel = jours[c.get(Calendar.DAY_OF_WEEK) - 1];

        if (!jourActuel.equalsIgnoreCase(jour)) {
            return (c.get(Calendar.DAY_OF_WEEK) < jourEnNumero(jour)) ? "À venir" : "Terminé";
        }

        // Comparer l'heure
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date now = new Date();
            Date hDebut = sdf.parse(debut);
            Date hFin = sdf.parse(fin);

            if (now.after(hDebut) && now.before(hFin)) {
                return "En cours";
            } else if (now.before(hDebut)) {
                return "À venir";
            } else {
                return "Terminé";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Inconnu";
        }
    }

    // Convertir nom du jour en numéro
    private static int jourEnNumero(String jour) {
        switch(jour.toLowerCase()) {
            case "dimanche": return 1;
            case "lundi": return 2;
            case "mardi": return 3;
            case "mercredi": return 4;
            case "jeudi": return 5;
            case "vendredi": return 6;
            case "samedi": return 7;
            default: return 0;
        }
    }

    private void loadEmploi() {

        long studentId = TokenManager.getInstance(requireContext()).getStudentId();

        if (studentId == -1) {
            Toast.makeText(requireContext(),
                    "Étudiant non connecté",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Call<EmploiEtudiantResponse> call =
                EmploiRetrofitClient.getInstance()
                        .getEmploiApiService()
                        .getEmploiByStudentId(studentId);

        call.enqueue(new Callback<EmploiEtudiantResponse>() {
            @Override
            public void onResponse(Call<EmploiEtudiantResponse> call,
                                   Response<EmploiEtudiantResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<EmploiWrapperDTO> wrappers = response.body().getEmplois();

                    data.clear();
                    for (EmploiWrapperDTO w : wrappers) {
                        EmploiDuTempsDTO e = w.toEmploiDTO();

                        if (e.getJour() != null && e.getJour().equalsIgnoreCase(jour)) {
                            e.setEtat(calculerEtat(e.getJour(), e.getCreneauDebut(), e.getCreneauFin()));

                            data.add(e);

                        }
                    }
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onFailure(Call<EmploiEtudiantResponse> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Erreur réseau",
                        Toast.LENGTH_LONG).show();
            }
        });
    }



}
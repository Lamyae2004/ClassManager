package com.ensa.mobile.emploitemps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ensa.mobile.R;
import com.ensa.mobile.emploitemps.api.EmploiApiService;
import com.ensa.mobile.emploitemps.api.EmploiRetrofitClient;
import com.ensa.mobile.emploitemps.models.EmploiDuTempsDTO;
import com.ensa.mobile.emploitemps.models.SeanceActionRequest;
import com.ensa.mobile.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmploiAdapter extends RecyclerView.Adapter<EmploiAdapter.ViewHolder> {

    private List<EmploiDuTempsDTO> data;
    private boolean isTeacher;

    public EmploiAdapter(List<EmploiDuTempsDTO> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cellule_emploi, parent, false);

        Context context = parent.getContext();
        TokenManager tokenManager = TokenManager.getInstance(context);
        isTeacher = tokenManager.getRole() != null &&
                tokenManager.getRole().equalsIgnoreCase("TEACHER");

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EmploiDuTempsDTO e = data.get(position);

        holder.txtCreneau.setText(e.getCreneauDebut() + " - " + e.getCreneauFin());
        holder.txtMatiere.setText(e.getMatiereNom());
        holder.txtProf.setText(e.getProfNom());
        holder.txtSalle.setText(e.getSalleNom());
        holder.txtStatus.setText(e.getEtat());

        // Couleur du statut
        switch (e.getEtat()) {
            case "En cours":
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_en_cours);
                holder.txtStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
                break;
            case "À venir":
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_a_venir);
                holder.txtStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
                break;
            case "Terminé":
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_termine);
                holder.txtStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
                break;
            default:
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_termine);
                holder.txtStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
                break;
        }

        // Afficher les boutons UNIQUEMENT pour les professeurs
        if (isTeacher) {
            holder.layoutActionsProf.setVisibility(View.VISIBLE);

            // Action : Déclarer un retard
            holder.btnDeclarerRetard.setOnClickListener(v -> {
                showRetardDialog(holder.itemView.getContext(), e);
            });

            // Action : Annuler la séance
            holder.btnAnnulerSeance.setOnClickListener(v -> {
                showAnnulationDialog(holder.itemView.getContext(), e, position);
            });
        } else {
            holder.layoutActionsProf.setVisibility(View.GONE);
        }
    }

    // Dialog pour déclarer un retard (pour PROF uniquement)
    private void showRetardDialog(Context context, EmploiDuTempsDTO emploi) {
        new AlertDialog.Builder(context)
                .setTitle("Déclarer un retard")
                .setMessage("Voulez-vous signaler un retard pour le cours de " + emploi.getMatiereNom() + " ?")
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    // ✅ Utiliser emploi.getId() qui existe dans EmploiDuTempsDTO
                    declareRetard(context, emploi.getId(), emploi.getMatiereNom());
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // Dialog pour annuler une séance (pour PROF uniquement)
    private void showAnnulationDialog(Context context, EmploiDuTempsDTO emploi, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Annuler la séance")
                .setMessage("Êtes-vous sûr de vouloir annuler le cours de " + emploi.getMatiereNom() + " ?")
                .setPositiveButton("Oui, annuler", (dialog, which) -> {
                    // ✅ Utiliser emploi.getId() qui existe dans EmploiDuTempsDTO
                    annulerSeance(context, emploi.getId(), emploi.getMatiereNom(), position);
                })
                .setNegativeButton("Non", null)
                .show();
    }

    // Méthode pour déclarer le retard via l'API (utilise l'ID)
    private void declareRetard(Context context, Long emploiId, String matiereNom) {
        SeanceActionRequest request = new SeanceActionRequest();
        request.setEmploiId(emploiId);
        request.setAction("RETARD");
        request.setMotif("Retard signalé depuis l'app mobile");

        EmploiApiService api = EmploiRetrofitClient.getInstance().getEmploiApiService();
        api.declareAction(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    Boolean success = (Boolean) body.get("success");
                    String message = (String) body.get("message");

                    if (success != null && success) {
                        Toast.makeText(context, message != null ? message : "Retard déclaré avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(context, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Méthode pour annuler la séance via l'API (utilise l'ID)
    private void annulerSeance(Context context, Long emploiId, String matiereNom, int position) {
        SeanceActionRequest request = new SeanceActionRequest();
        request.setEmploiId(emploiId);
        request.setAction("ANNULER");
        request.setMotif("Séance annulée depuis l'app mobile");

        EmploiApiService api = EmploiRetrofitClient.getInstance().getEmploiApiService();
        api.declareAction(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    Boolean success = (Boolean) body.get("success");
                    String message = (String) body.get("message");

                    if (success != null && success) {
                        Toast.makeText(context, message != null ? message : "Séance annulée avec succès", Toast.LENGTH_SHORT).show();

                        // Optionnel : retirer l'item de la liste
                        // data.remove(position);
                        // notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(context, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCreneau, txtMatiere, txtProf, txtSalle, txtStatus;
        LinearLayout layoutActionsProf;
        MaterialButton btnDeclarerRetard, btnAnnulerSeance;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            txtCreneau = v.findViewById(R.id.txtCreneau);
            txtMatiere = v.findViewById(R.id.txtMatiere);
            txtProf = v.findViewById(R.id.txtProf);
            txtSalle = v.findViewById(R.id.txtSalle);
            txtStatus = v.findViewById(R.id.txtStatus);
            cardView = v.findViewById(R.id.cardView);
            layoutActionsProf = v.findViewById(R.id.layoutActionsProf);
            btnDeclarerRetard = v.findViewById(R.id.btnDeclarerRetard);
            btnAnnulerSeance = v.findViewById(R.id.btnAnnulerSeance);
        }
    }
}
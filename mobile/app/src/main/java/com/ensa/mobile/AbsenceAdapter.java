package com.ensa.mobile;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.mobile.utils.Absence;

import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.ViewHolder> {

    private List<Absence> absences;

    public AbsenceAdapter(List<Absence> absences) {
        this.absences = absences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absence absence = absences.get(position);

        holder.txtMatiere.setText(absence.matiere);
        holder.txtDate.setText(absence.date + " | " + absence.heure);

        // Gestion de l'état + badge couleur
        switch (absence.etat) {
            case "NON_JUSTIFIEE":
                holder.txtEtat.setText("Non justifiée");
                holder.txtEtat.setTextColor(Color.RED);
                holder.btnJustifier.setEnabled(true);
                holder.btnJustifier.setText("Justifier");
                break;

            case "EN_ATTENTE":
                holder.txtEtat.setText("En attente");
                holder.txtEtat.setTextColor(Color.parseColor("#FFA500"));
                holder.btnJustifier.setEnabled(false);
                holder.btnJustifier.setText("En attente");
                break;

            case "VALIDEE":
                holder.txtEtat.setText("Validée");
                holder.txtEtat.setTextColor(Color.GREEN);
                holder.btnJustifier.setEnabled(false);
                holder.btnJustifier.setText("Validée");
                break;
        }

        // Clic sur Justifier (uniquement si NON_JUSTIFIEE)
        holder.btnJustifier.setOnClickListener(v -> {

            if (!absence.etat.equals("NON_JUSTIFIEE")) return;

            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_justifier_absence, null);

            TextView txtInfo = dialogView.findViewById(R.id.txtInfoAbsence);
            EditText edtMotif = dialogView.findViewById(R.id.edtMotif);
            Button btnJoindre = dialogView.findViewById(R.id.btnJoindre);

            txtInfo.setText(
                    absence.matiere + "\n" +
                            absence.date + " | " + absence.heure
            );

            btnJoindre.setOnClickListener(v1 ->
                    Toast.makeText(v1.getContext(),
                            "Fichier joint (simulation)",
                            Toast.LENGTH_SHORT).show()
            );

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Justifier l'absence")
                    .setView(dialogView)
                    .setPositiveButton("Envoyer", (dialog, which) -> {
                        absence.etat = "EN_ATTENTE";
                        notifyItemChanged(holder.getAdapterPosition());

                        Toast.makeText(v.getContext(),
                                "Justification envoyée (en attente)",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return absences.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMatiere, txtDate, txtEtat;
        Button btnJustifier;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMatiere = itemView.findViewById(R.id.txtMatiere);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtEtat = itemView.findViewById(R.id.txtEtat);
            btnJustifier = itemView.findViewById(R.id.btnJustifier);
        }
    }
}

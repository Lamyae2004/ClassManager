package com.ensa.mobile.emploitemps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ensa.mobile.R;
import com.ensa.mobile.emploitemps.models.EmploiDuTempsDTO;
import java.util.List;

public class EmploiAdapter extends RecyclerView.Adapter<EmploiAdapter.ViewHolder> {

    private List<EmploiDuTempsDTO> data;

    public EmploiAdapter(List<EmploiDuTempsDTO> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cellule_emploi, parent, false);
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


    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCreneau, txtMatiere, txtProf, txtSalle, txtStatus;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            txtCreneau = v.findViewById(R.id.txtCreneau);
            txtMatiere = v.findViewById(R.id.txtMatiere);
            txtProf = v.findViewById(R.id.txtProf);
            txtSalle = v.findViewById(R.id.txtSalle);
            cardView = v.findViewById(R.id.cardView);
            txtStatus = v.findViewById(R.id.txtStatus);
        }
    }
}

package com.ensa.mobile.gestionDocuments.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.mobile.R;
import com.ensa.mobile.gestionDocuments.models.Document;
import com.ensa.mobile.gestionDocuments.models.MatiereProfDto;

import java.util.List;
import java.util.Map;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documents;
    private Context context;
    private Map<Long, MatiereProfDto> matiereIdMap; // Map matiereId -> MatiereProfDto

    public DocumentAdapter(List<Document> documents, Context context, Map<Long, MatiereProfDto> matiereIdMap) {
        this.documents = documents;
        this.context = context;
        this.matiereIdMap = matiereIdMap;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document doc = documents.get(position);

        holder.tvNom.setText(doc.getNom());
        holder.tvType.setText("Type : " + doc.getType());

        // Récupérer la matière et le prof depuis la map
        MatiereProfDto mp = matiereIdMap.get(doc.getMatiereId());
        if (mp != null) {
            String matiereName = mp.getMatiere();
            String profName = mp.getProfNom() + " " + mp.getProfPrenom();
            holder.tvMatiere.setText( matiereName + " - Prof : " + profName);
        } else {
            holder.tvMatiere.setText("Matière : Inconnue - Prof : Inconnu");
        }

        holder.btnDownload.setOnClickListener(v -> {
            Toast.makeText(context, "Téléchargement lancé", Toast.LENGTH_SHORT).show();
            downloadDocument(doc.getUrl(), doc.getNom());
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvType, tvMatiere;
        ImageButton btnDownload;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tv_nom_document);
            tvType = itemView.findViewById(R.id.tv_type_document);
            tvMatiere = itemView.findViewById(R.id.tv_matiere_document);
            btnDownload = itemView.findViewById(R.id.btn_download);
        }
    }

    // Mettre à jour la liste lors du filtrage
    public void updateList(List<Document> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }

    // Télécharger le document
    private void downloadDocument(String url, String nom) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(nom);
            request.setDescription("Téléchargement en cours...");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nom);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

            Toast.makeText(context, "Téléchargement de " + nom, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show();
        }
    }
}

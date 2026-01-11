package com.ensa.mobile.absence.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.mobile.R;
import com.ensa.mobile.absence.api.AbsenceRetrofitClient;
import com.ensa.mobile.absence.models.AbsenceResponse;
import com.ensa.mobile.absence.ui.FragmentAbsenceEtudiant;
import com.ensa.mobile.absence.models.Absence;
import com.ensa.mobile.utils.FilePickerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.ViewHolder> {

    private List<Absence> absences;
    private Activity activity;
    private FragmentAbsenceEtudiant fragment; // ✅ Référence au fragment
    private static final int REQUEST_CODE_PICK_FILE = 1001;
    private static final int REQUEST_CODE_CAMERA = 1002;
    private static final int REQUEST_CODE_GALLERY = 1003;
    private Long currentAbsenceId;
    private int currentPosition;

    // ✅ Ajout : Variables pour garder la référence au dialog et au bouton
    private AlertDialog currentDialog;
    private Button currentBtnJoindre;
    private TextView currentTxtFileName;
    private Uri selectedFileUri;
    private String selectedFileName;

    // ✅ Constructeur pour Fragment (avec ActivityResultLauncher)
    public AbsenceAdapter(List<Absence> absences, Activity activity, FragmentAbsenceEtudiant fragment) {
        this.absences = absences;
        this.activity = activity;
        this.fragment = fragment;
    }

    // ✅ Constructeur pour Activity (ancien système)
    public AbsenceAdapter(List<Absence> absences, Activity activity) {
        this.absences = absences;
        this.activity = activity;
        this.fragment = null; // Pas de fragment dans ce cas
    }

    public void updateAbsences(List<Absence> newAbsences) {
        this.absences = newAbsences;
        notifyDataSetChanged();
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

        holder.txtMatiere.setText(absence.matiere != null ? absence.matiere : "Matière");
        holder.txtDate.setText(absence.date != null ? absence.date : "");
        holder.txtTime.setText(absence.heure != null ? absence.heure : "");

        // Gestion de l'état + badge couleur
        switch (absence.etat) {
            case "NON_JUSTIFIEE":
                holder.txtEtat.setText("Non justifiée");
                holder.txtEtat.setBackgroundResource(R.drawable.bg_status_non_justifiee);
                holder.txtEtat.setTextColor(Color.WHITE);
                holder.btnJustifier.setEnabled(true);
                holder.btnJustifier.setText("Justifier");
                break;

            case "EN_ATTENTE":
                holder.txtEtat.setText("En attente");
                holder.txtEtat.setBackgroundResource(R.drawable.bg_status_en_attente);
                holder.txtEtat.setTextColor(Color.WHITE);
                holder.btnJustifier.setEnabled(false);
                holder.btnJustifier.setText("En attente");
                break;

            case "VALIDEE":
                holder.txtEtat.setText("Validée");
                holder.txtEtat.setBackgroundResource(R.drawable.bg_status_validee);
                holder.txtEtat.setTextColor(Color.WHITE);
                holder.btnJustifier.setEnabled(false);
                holder.btnJustifier.setText("Validée");
                break;
        }

        // Clic sur Justifier
        holder.btnJustifier.setOnClickListener(v -> {
            if (!absence.etat.equals("NON_JUSTIFIEE")) return;

            currentAbsenceId = absence.id;
            currentPosition = holder.getAdapterPosition();
            selectedFileUri = null;
            selectedFileName = null;

            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_justifier_absence, null);

            TextView txtInfo = dialogView.findViewById(R.id.txtInfoAbsence);
            EditText edtMotif = dialogView.findViewById(R.id.edtMotif);
            currentBtnJoindre = dialogView.findViewById(R.id.btnJoindre);

            //  Nouveaux éléments du design moderne
            currentTxtFileName = dialogView.findViewById(R.id.txtFileName);
            View cardFileName = dialogView.findViewById(R.id.cardFileName);
            View btnRemoveFile = dialogView.findViewById(R.id.btnRemoveFile);

            if (cardFileName != null) {
                cardFileName.setVisibility(View.GONE);
            }

            // ✅ Bouton pour retirer le fichier
            if (btnRemoveFile != null) {
                btnRemoveFile.setOnClickListener(v2 -> {
                    selectedFileUri = null;
                    selectedFileName = null;
                    if (cardFileName != null) {
                        cardFileName.setVisibility(View.GONE);
                    }
                    if (currentBtnJoindre != null) {
                        currentBtnJoindre.setText("📁 Choisir un fichier");
                    }
                    if (currentDialog != null) {
                        currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                    Toast.makeText(activity, "Fichier retiré", Toast.LENGTH_SHORT).show();
                });
            }

            txtInfo.setText(
                    absence.matiere + "\n" +
                            absence.date + " | " + absence.heure
            );

            currentBtnJoindre.setOnClickListener(v1 -> {
                new AlertDialog.Builder(v1.getContext())
                        .setTitle("Choisir une source")
                        .setItems(new String[]{"Appareil photo", "Galerie", "Fichier PDF"}, (dialog, which) -> {
                            Intent intent;

                            // ✅ Si on utilise le Fragment avec ActivityResultLauncher
                            if (fragment != null) {
                                switch (which) {
                                    case 0: // Camera
                                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                            fragment.launchCamera(intent);
                                        }
                                        break;
                                    case 1: // Gallery
                                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                            fragment.launchGallery(intent);
                                        }
                                        break;
                                    case 2: // File Picker
                                        intent = FilePickerHelper.createFilePickerIntent();
                                        fragment.launchFilePicker(intent);
                                        break;
                                }
                            }
                            // ✅ Si on utilise une Activity normale (ancien système)
                            else {
                                int requestCode;
                                switch (which) {
                                    case 0: // Camera
                                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        requestCode = REQUEST_CODE_CAMERA;
                                        break;
                                    case 1: // Gallery
                                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        requestCode = REQUEST_CODE_GALLERY;
                                        break;
                                    case 2: // File Picker
                                        intent = FilePickerHelper.createFilePickerIntent();
                                        requestCode = REQUEST_CODE_PICK_FILE;
                                        break;
                                    default:
                                        return;
                                }

                                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivityForResult(intent, requestCode);
                                } else {
                                    Toast.makeText(activity, "Aucune application disponible", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            });

            // ✅ Créer le dialog avec le bouton "Envoyer"
            currentDialog = new AlertDialog.Builder(v.getContext())
                    .setView(dialogView)
                    .setPositiveButton("Envoyer", (dialog, which) -> {
                        // Envoyer la justification
                        if (selectedFileUri != null && selectedFileName != null) {
                            String motif = edtMotif.getText().toString().trim();
                            uploadJustification(selectedFileUri, selectedFileName, currentPosition, motif);
                        } else {
                            Toast.makeText(activity, "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();

            // ✅ Désactiver le bouton Envoyer au début
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        });
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity, "Sélection annulée", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = null;
        String fileName = null;

        if (requestCode == REQUEST_CODE_CAMERA && data != null) {
            android.graphics.Bitmap photo = (android.graphics.Bitmap) data.getExtras().get("data");
            if (photo != null) {
                try {
                    File photoFile = new File(activity.getCacheDir(), "photo_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    photo.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    fileUri = Uri.fromFile(photoFile);
                    fileName = photoFile.getName();
                } catch (Exception e) {
                    Toast.makeText(activity, "Erreur lors de la sauvegarde de la photo", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                fileName = FilePickerHelper.getFileName(activity, fileUri);
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "image_" + System.currentTimeMillis() + ".jpg";
                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_FILE && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                Log.d("FilePicker", "URI: " + fileUri);

                fileName = FilePickerHelper.getFileName(activity, fileUri);
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "file_" + System.currentTimeMillis() + ".pdf";
                }
            } else {
                Toast.makeText(activity, "Fichier non sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // ✅ Sauvegarder l'URI et le nom du fichier
        if (fileUri != null && fileName != null) {
            selectedFileUri = fileUri;
            selectedFileName = fileName;

            // ✅ Mettre à jour l'interface du dialog
            updateDialogWithFile(fileName);
        } else {
            Toast.makeText(activity, "Erreur: fichier non sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    // Nouvelle méthode : Met à jour le dialog avec le fichier sélectionné
    private void updateDialogWithFile(String fileName) {
        // Trouver la card et la rendre visible
        if (currentDialog != null) {
            View dialogView = currentDialog.findViewById(android.R.id.content);
            if (dialogView != null) {
                View cardFileName = dialogView.findViewById(R.id.cardFileName);
                if (cardFileName != null) {
                    cardFileName.setVisibility(View.VISIBLE);
                }
            }
        }

        // Mettre à jour le texte du fichier
        if (currentTxtFileName != null) {
            currentTxtFileName.setText(fileName);
        }

        // Changer le texte du bouton
        if (currentBtnJoindre != null) {
            currentBtnJoindre.setText(" Fichier sélectionné");
        }

        // Activer le bouton Envoyer
        if (currentDialog != null) {
            currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }

        Toast.makeText(activity,  fileName + " prêt à envoyer", Toast.LENGTH_SHORT).show();
    }

    private void uploadJustification(Uri fileUri, String fileName, int position, String motif) {
        try {
            File file = FilePickerHelper.createFileFromUri(activity, fileUri, fileName);
            if (file == null) {
                Toast.makeText(activity, "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT).show();
                return;
            }

            // Déterminer le type MIME
            String mimeType = "application/octet-stream";
            if (fileName != null) {
                String lower = fileName.toLowerCase();
                if (lower.endsWith(".pdf")) {
                    mimeType = "application/pdf";
                } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (lower.endsWith(".png")) {
                    mimeType = "image/png";
                } else if (lower.endsWith(".gif")) {
                    mimeType = "image/gif";
                }
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            Call<AbsenceResponse> call = AbsenceRetrofitClient.getInstance()
                    .getAbsenceApiService()
                    .uploadJustification(currentAbsenceId, body);

            Toast.makeText(activity, "Envoi en cours...", Toast.LENGTH_SHORT).show();

            call.enqueue(new Callback<AbsenceResponse>() {
                @Override
                public void onResponse(Call<AbsenceResponse> call, Response<AbsenceResponse> response) {
                    if (response.isSuccessful()) {
                        Absence absence = absences.get(position);
                        absence.etat = "EN_ATTENTE";
                        absence.fileName = fileName;
                        notifyItemChanged(position);
                        Toast.makeText(activity, "Justification envoyée avec succès", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, "Erreur: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AbsenceResponse> call, Throwable t) {
                    Toast.makeText(activity, "Erreur de connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AbsenceAdapter", "Upload failed", t);
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("AbsenceAdapter", "Upload error", e);
        }
    }

    @Override
    public int getItemCount() {
        return absences.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMatiere, txtDate, txtTime, txtEtat;
        Button btnJustifier;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMatiere = itemView.findViewById(R.id.txtMatiere);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtEtat = itemView.findViewById(R.id.txtEtat);
            btnJustifier = itemView.findViewById(R.id.btnJustifier);
        }
    }
}
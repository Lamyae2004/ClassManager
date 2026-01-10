package com.ensa.mobile.gestionDocuments.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import okhttp3.ResponseBody;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ensa.mobile.R;
import com.ensa.mobile.gestionDocuments.api.ClasseRetrofitClient;
import com.ensa.mobile.gestionDocuments.api.DocumentRetrofitClient;
import com.ensa.mobile.gestionDocuments.api.MatiereRetrofitClient;
import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.MatiereDto;
import com.ensa.mobile.utils.TokenManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDocumentFragment extends Fragment {

    private Spinner documentClasseSpinner, documentModuleSpinner, documentTypeSpinner;
    private EditText documentTitleEditText;
    private Button documentSelectFileButton, documentUploadButton;

    private List<ClasseDto> classesList = new ArrayList<>();
    private List<MatiereDto> matieresList = new ArrayList<>();
    private Uri selectedFileUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_upload_document, container, false);

        documentClasseSpinner = view.findViewById(R.id.documentClasse);
        documentModuleSpinner = view.findViewById(R.id.documentModule);
        documentTypeSpinner = view.findViewById(R.id.documentType);
        documentTitleEditText = view.findViewById(R.id.documentTitle);
        documentSelectFileButton = view.findViewById(R.id.documentSelectFile);
        documentUploadButton = view.findViewById(R.id.documentUploadButton);

        documentSelectFileButton.setOnClickListener(v -> selectFile());
        documentUploadButton.setOnClickListener(v -> {
            if (selectedFileUri != null) uploadDocument(selectedFileUri);
            else Toast.makeText(getContext(), "Choisissez un fichier d'abord", Toast.LENGTH_SHORT).show();
        });

        loadClasses();

        documentClasseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadMatieres(classesList.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return view;
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            documentSelectFileButton.setText(fileName);

        }
    }

    private void loadClasses() {
        Long profId = TokenManager.getInstance(requireContext()).getTeacherId();
        ClasseRetrofitClient.getInstance()
                .getClasseApi()
                .getClassesByProfessor(profId)
                .enqueue(new Callback<List<ClasseDto>>() {
                    @Override
                    public void onResponse(Call<List<ClasseDto>> call, Response<List<ClasseDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            classesList = response.body();
                            List<String> spinnerItems = new ArrayList<>();
                            for (ClasseDto c : classesList) spinnerItems.add(c.getNom() + " - " + c.getFiliere());
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            documentClasseSpinner.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ClasseDto>> call, Throwable t) {
                        Toast.makeText(getContext(), "Erreur chargement classes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMatieres(Long classeId) {
        Long profId = TokenManager.getInstance(requireContext()).getTeacherId();
        MatiereRetrofitClient.getInstance()
                .getMatiereApi()
                .getMatiereByClasseAndTeacher(classeId, profId)
                .enqueue(new Callback<List<MatiereDto>>() {
                    @Override
                    public void onResponse(Call<List<MatiereDto>> call, Response<List<MatiereDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            matieresList = response.body();
                            List<String> spinnerItems = new ArrayList<>();
                            for (MatiereDto m : matieresList) spinnerItems.add(m.getNom());
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            documentModuleSpinner.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<MatiereDto>> call, Throwable t) {
                        Toast.makeText(getContext(), "Erreur chargement matières", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadDocument(Uri fileUri) {
        try {

            String fileName = getFileName(fileUri);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                Toast.makeText(getContext(), "Impossible de lire le fichier", Toast.LENGTH_SHORT).show();
                return;
            }

            // Déterminer le type MIME
            String mimeType = requireContext().getContentResolver().getType(fileUri);
            if (mimeType == null) mimeType = "application/octet-stream";


            String finalMimeType = mimeType;
            RequestBody filePart = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse(finalMimeType);
                }

                @Override
                public void writeTo(okio.BufferedSink sink) throws IOException {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, read);
                    }
                    inputStream.close();
                }
            };

            MultipartBody.Part fileBody = MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    filePart
            );

            RequestBody titlePart = RequestBody.create(
                    documentTitleEditText.getText().toString(),
                    MediaType.parse("text/plain")
            );

            RequestBody typePart = RequestBody.create(
                    documentTypeSpinner.getSelectedItem().toString().toUpperCase(),
                    MediaType.parse("text/plain")
            );

            Long matiereId = matieresList.get(documentModuleSpinner.getSelectedItemPosition()).getId();
            Long classeId = classesList.get(documentClasseSpinner.getSelectedItemPosition()).getId();
            Long profId = TokenManager.getInstance(requireContext()).getTeacherId();

            // Appel Retrofit
            DocumentRetrofitClient.getInstance()
                    .getDocumentApi()
                    .uploadDocument(
                            titlePart,
                            typePart,
                            RequestBody.create(String.valueOf(matiereId), MediaType.parse("text/plain")),
                            RequestBody.create(String.valueOf(classeId), MediaType.parse("text/plain")),
                            RequestBody.create(String.valueOf(profId), MediaType.parse("text/plain")),
                            RequestBody.create(fileName, MediaType.parse("text/plain")),
                            fileBody
                    )
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    String message = response.body().string(); // récupérer le message envoyé par Spring
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Upload réussi mais impossible de lire la réponse", Toast.LENGTH_SHORT).show();
                                }
                                resetForm();
                            } else {
                                Toast.makeText(getContext(), "Erreur serveur : " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur upload fichier", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void resetForm() {

        documentTitleEditText.setText("");

        if (documentClasseSpinner.getAdapter() != null)
            documentClasseSpinner.setSelection(0);

        if (documentModuleSpinner.getAdapter() != null)
            documentModuleSpinner.setSelection(0);

        if (documentTypeSpinner.getAdapter() != null)
            documentTypeSpinner.setSelection(0);

        selectedFileUri = null;

        documentSelectFileButton.setText("Choisir un fichier");
    }



}

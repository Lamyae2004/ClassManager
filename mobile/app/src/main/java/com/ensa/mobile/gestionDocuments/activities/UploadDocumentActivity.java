package com.ensa.mobile.gestionDocuments.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ensa.mobile.R;

public class UploadDocumentActivity extends AppCompatActivity {


    private Spinner documentClasseSpinner;
    private Spinner documentModuleSpinner;
    private EditText documentTitleEditText;
    private Spinner documentTypeSpinner;
    private Button documentSelectFileButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_document);
        documentClasseSpinner = findViewById(R.id.documentClasse);
        documentModuleSpinner = findViewById(R.id.documentModule);
        documentTitleEditText = findViewById(R.id.documentTitle);
        documentTypeSpinner = findViewById(R.id.documentType);
        documentSelectFileButton = findViewById(R.id.documentSelectFile);



    }
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            // Tu peux maintenant utiliser le fichierUri pour l'envoyer ou afficher le nom
            Toast.makeText(this, "Fichier choisi: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }
}
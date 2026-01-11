package com.ensa.mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FilePickerHelper {
    private static final String TAG = "FilePickerHelper";

    public static final int REQUEST_CODE_PICK_FILE = 1001;
    public static final int REQUEST_CODE_CAMERA = 1002;
    public static final int REQUEST_CODE_GALLERY = 1003;

    /**
     * Crée un Intent pour sélectionner un fichier (PDF ou image)
     */
    public static Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Limite aux PDF et images
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        return Intent.createChooser(intent, "Sélectionner un fichier");
    }

    /**
     * Crée un Intent pour prendre une photo
     */
    public static Intent createCameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    /**
     * Crée un Intent pour sélectionner une image depuis la galerie
     */
    public static Intent createImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    /**
     * Récupère le nom du fichier depuis l'URI
     * Compatible avec Google Drive et stockage local
     */
    public static String getFileName(Context context, Uri uri) {
        if (uri == null) {
            Log.e(TAG, "URI est null");
            return null;
        }

        String result = null;

        // Méthode 1 : ContentResolver (fonctionne avec Google Drive)
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            android.database.Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                        Log.d(TAG, "Nom du fichier récupéré: " + result);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la récupération du nom", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Méthode 2 : Extraire depuis le path
        if (result == null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1) {
                    result = path.substring(cut + 1);
                }
            }
        }

        // Fallback si aucune méthode n'a fonctionné
        if (result == null || result.isEmpty()) {
            result = "file_" + System.currentTimeMillis();
            Log.w(TAG, "Nom de fichier généré automatiquement: " + result);
        }

        return result;
    }

    /**
     * Crée un fichier temporaire depuis l'URI
     * ESSENTIEL pour Google Drive et autres content providers
     */
    public static File createFileFromUri(Context context, Uri uri, String fileName) {
        if (uri == null) {
            Log.e(TAG, "URI est null");
            return null;
        }

        if (fileName == null || fileName.isEmpty()) {
            fileName = "temp_file_" + System.currentTimeMillis();
            Log.w(TAG, "Nom de fichier généré: " + fileName);
        }

        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Ouvre l'InputStream via ContentResolver
            inputStream = context.getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                Log.e(TAG, "Impossible d'ouvrir l'InputStream pour: " + uri);
                return null;
            }

            // Crée le fichier dans le cache
            File file = new File(context.getCacheDir(), fileName);
            outputStream = new FileOutputStream(file);

            // Copie les données
            byte[] buffer = new byte[8192]; // Buffer plus grand = plus rapide
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            outputStream.flush();

            Log.d(TAG, "Fichier créé: " + file.getAbsolutePath() +
                    " (Taille: " + formatFileSize(totalBytes) + ")");

            return file;

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la création du fichier", e);
            return null;

        } finally {
            // Fermeture garantie des streams
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    /**
     * Ferme un stream sans lancer d'exception
     */
    private static void closeQuietly(java.io.Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la fermeture", e);
            }
        }
    }

    /**
     * Formate la taille d'un fichier en format lisible
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Obtient la taille du fichier depuis l'URI
     */
    public static long getFileSize(Context context, Uri uri) {
        long size = -1;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            android.database.Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex >= 0) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la récupération de la taille", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return size;
    }
}
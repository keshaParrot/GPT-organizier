package com.example.gptorganizier.service;

import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class GoogleDriveSyncService {

    private static GoogleDriveSyncService instance;
    private Drive driveService;
    private static final String TAG = "GoogleDriveHelper";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private GoogleDriveSyncService(GoogleAccountCredential credential) {
        this.driveService = new Drive.Builder(
                new NetHttpTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName("YourAppName")
                .build();
    }

    public static synchronized GoogleDriveSyncService getInstance(GoogleAccountCredential credential) {
        if (instance == null) {
            instance = new GoogleDriveSyncService(credential);
        }
        return instance;
    }

    public void uploadDatabase(String databasePath) {
        java.io.File file = new java.io.File(databasePath);
        if (!file.exists()) {
            Log.e(TAG, "Database file does not exist.");
            return;
        }

        new Thread(() -> {
            try {
                FileContent mediaContent = new FileContent("application/octet-stream", file);
                File fileMetadata = new File();
                fileMetadata.setName("my_database.db");

                driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
                Log.d(TAG, "Database uploaded successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Error uploading database: " + e.getMessage());
            }
        }).start();
    }


    public void downloadDatabase(String destinationPath) {
        new Thread(() -> {
            try {
                FileList result = driveService.files().list()
                        .setQ("name = 'my_database.db'")
                        .setSpaces("drive")
                        .setFields("files(id, name)")
                        .execute();

                if (!result.getFiles().isEmpty()) {
                    String fileId = result.getFiles().get(0).getId();

                    InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();

                    java.io.File localFile = new java.io.File(destinationPath);
                    OutputStream outputStream = Files.newOutputStream(localFile.toPath());

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    Log.d(TAG, "Database downloaded successfully.");
                } else {
                    Log.e(TAG, "Database file not found on Google Drive.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading database: " + e.getMessage());
            }
        }).start();
    }
}

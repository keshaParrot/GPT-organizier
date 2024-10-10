package com.keshaparrot.gptorganizier.service;

import android.content.Context;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.domain.Record;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import com.keshaparrot.gptorganizier.database.DatabaseService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO we need to register app in google cloud console
public class GoogleDriveSyncService {

    private static final String TAG = "GoogleDriveSyncService";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String DATABASE_FILE_NAME = "records.db";

    private static GoogleDriveSyncService instance;
    private final DatabaseService databaseService;
    private final Drive googleDriveService;
    private final Context context;


    private GoogleDriveSyncService(Context context)  {
        this.context = context;
        GoogleAuthService googleAuthService = GoogleAuthService.getInstance();
        GoogleAccountCredential credential = googleAuthService.getCredential();

        Drive.Builder builder = new Drive.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential);
        builder.setApplicationName(context.getString(R.string.app_name));
        this.googleDriveService = builder.build();
        this.databaseService = DatabaseService.getInstance(context);
    }

    //TODO i get some error here while downloading DB
    public static synchronized GoogleDriveSyncService getInstance(Context context){
        if (instance == null) {
            instance = new GoogleDriveSyncService(context);
        }
        return instance;
    }

    public static synchronized GoogleDriveSyncService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GoogleDriveSyncService is not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    public void downloadDatabaseFromGoogleDrive() {
        executorService.execute(() -> {
            try {
                FileList result = googleDriveService.files().list()
                        .setQ("name='" + DATABASE_FILE_NAME + "' and trashed=false")
                        .setSpaces("drive")
                        .setFields("files(id, name)")
                        .execute();

                List<File> files = result.getFiles();
                if (files != null && !files.isEmpty()) {
                    File file = files.get(0);
                    Log.d(TAG, "Found database file: " + file.getName());

                    try (InputStream inputStream = googleDriveService.files().get(file.getId()).executeMediaAsInputStream();
                         FileOutputStream outputStream = new FileOutputStream(context.getDatabasePath(DATABASE_FILE_NAME))) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        Log.d(TAG, "Database downloaded successfully from Google Drive.");
                    }
                } else {
                    Log.d(TAG, "No database file found on Google Drive.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading database from Google Drive", e);
            }
        });
    }

    public void syncWithGoogleDrive() {
        executorService.execute(() -> {
            try {
                FileList result = googleDriveService.files().list()
                        .setQ("name='" + DATABASE_FILE_NAME + "' and trashed=false")
                        .setSpaces("drive")
                        .setFields("files(id, name)")
                        .execute();

                List<File> files = result.getFiles();
                if (files != null && !files.isEmpty()) {
                    File file = files.get(0);
                    Log.d("GoogleDriveSyncService", "Found database file: " + file.getName());

                    InputStream inputStream = googleDriveService.files().get(file.getId()).executeMediaAsInputStream();
                    List<Record> googleDriveRecords = extractRecordsFromInputStream(inputStream);

                    LiveData<List<Record>> liveLocalRecords = databaseService.getAll();
                    liveLocalRecords.observeForever(new Observer<List<Record>>() {
                        @Override
                        public void onChanged(List<Record> localRecords) {
                            synchronizeRecords(localRecords, googleDriveRecords);
                            liveLocalRecords.removeObserver(this); // Remove observer to prevent multiple syncs
                        }
                    });

                    inputStream.close();
                } else {
                    Log.d("GoogleDriveSyncService", "No database file found on Google Drive.");
                }
            } catch (Exception e) {
                Log.e("GoogleDriveSyncService", "Error synchronizing with Google Drive", e);
            }
        });
    }

    private List<Record> extractRecordsFromInputStream(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            Type recordListType = new TypeToken<List<Record>>() {}.getType();
            return new Gson().fromJson(reader, recordListType);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing records from InputStream", e);
            return Collections.emptyList();
        }
    }

    private void synchronizeRecords(List<Record> localRecords, List<Record> googleDriveRecords) {
        for (Record googleDriveRecord : googleDriveRecords) {
            boolean found = false;
            for (Record localRecord : localRecords) {
                if (localRecord.getId().equals(googleDriveRecord.getId())) {
                    found = true;
                    if (localRecord.getUpdateTime().before(googleDriveRecord.getUpdateTime())) {
                        databaseService.insert(googleDriveRecord);
                    }
                    break;
                }
            }
            if (!found) {
                databaseService.insert(googleDriveRecord);
            }
        }
        Log.d(TAG, "Records synchronized successfully.");
    }
}

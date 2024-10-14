package com.keshaparrot.gptorganizier.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.model.FileList;
import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.domain.Record;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.keshaparrot.gptorganizier.database.DatabaseService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleDriveSyncService {

    private static final String TAG = "GoogleDriveSyncService";
    private static final String DATABASE_FILE_NAME = "records.json";
    private static final String DATABASE_FOLDER_NAME = "GPT organizer data";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static GoogleDriveSyncService instance;

    private final DatabaseService databaseService;
    private final Drive googleDriveService;
    private final GoogleAccountCredential googleAccountCredential;
    private final Context context;

    private GoogleDriveSyncService(Context context)  {
        this.context = context;
        GoogleAuthService googleAuthService = GoogleAuthService.getInstance();
        this.googleAccountCredential = googleAuthService.getCredential();

        Drive.Builder builder = new Drive.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                googleAccountCredential);
        builder.setApplicationName(context.getString(R.string.app_name));
        this.googleDriveService = builder.build();
        this.databaseService = DatabaseService.getInstance(context);
    }

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

    public void syncGetDatabase() {
        DownloadDatabase(new DownloadCallback() {
            @Override
            public void onDownloadComplete(List<Record> records) {
                List<Record> dataToSave = databaseService.getUniqueRecords(records);
                databaseService.insertRecords(dataToSave);
                Log.d(TAG, "Database was updated with " + dataToSave.size() + " records.");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error during database download: " + errorMessage);
            }
        });
    }

    public void syncSendDatabase() {
        executorService.execute(() -> {
            DownloadDatabase(new DownloadCallback() {
                @Override
                public void onDownloadComplete(List<Record> records) {
                    List<Record> recordToSync = SortRecords(records);
                    UploadDatabase(packDataIntoJson(recordToSync));
                    Log.d(TAG, "Drive database was updated with " + recordToSync.size() + " records.");
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Error during database download: " + errorMessage);

                    List<Record> recordToSync = SortRecords(new ArrayList<>());
                    UploadDatabase(packDataIntoJson(recordToSync));
                    Log.d(TAG, "Drive database was updated with " + recordToSync.size() + " records.");
                }
            });
        });
    }

    private void DownloadDatabase(DownloadCallback callback) {
        executorService.execute(() -> {
            try {
                googleAccountCredential.getToken();
                String fileId = findFileInFolder(getOrCreateFolder());

                InputStream inputStream = googleDriveService.files().get(fileId)
                        .executeMediaAsInputStream();

                List<Record> dataList = parseJsonToDataList(inputStream);
                Log.d(TAG, "Downloaded and parsed JSON file with " + dataList.size() + " records.");
                callback.onDownloadComplete(dataList);
            } catch (UserRecoverableAuthException e) {
                Log.e(TAG, "User needs to accept consent.", e);
                context.startActivity(e.getIntent());
            } catch (Exception e) {
                Log.e(TAG, "Error downloading JSON file: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        });
    }
    private void UploadDatabase(java.io.File jsonFile) {
        executorService.execute(() -> {
            try {
                googleAccountCredential.getToken();

                String folderId = getOrCreateFolder();
                String fileId = findFileInFolder(folderId);

                ByteArrayContent content = new ByteArrayContent("application/json", Files.readAllBytes(jsonFile.toPath()));

                if (fileId != null) {
                    googleDriveService.files().update(fileId, null, content)
                            .setFields("id")
                            .set("parents", Collections.singletonList(folderId))
                            .execute();
                    Log.d(TAG, "File updated on Google Drive with ID: " + fileId);
                } else {
                    File fileMetadata = new File();
                    fileMetadata.setName(jsonFile.getName());
                    fileMetadata.setMimeType("application/json");
                    fileMetadata.setParents(Collections.singletonList(folderId));

                    File file = googleDriveService.files().create(fileMetadata, content)
                            .setFields("id")
                            .execute();

                    Log.d(TAG, "File uploaded to Google Drive with ID: " + file.getId());
                }
            } catch (UserRecoverableAuthIOException e) {
                Log.e(TAG, "User needs to authenticate again.", e);
                context.startActivity(e.getIntent());
            }catch (UserRecoverableAuthException e) {
                Log.e(TAG, "User needs to authenticate.", e);
                context.startActivity(e.getIntent());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file to Google Drive: " + e.getMessage());
            }
        });
    }
    private String findFileInFolder(String folderId) {
        String fileId = null;
        try {
            String query = "name = '" + DATABASE_FILE_NAME + "' and '" + folderId + "' in parents and trashed = false";

            FileList result = googleDriveService.files().list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setFields("files(id, name)")
                    .execute();

            if (result.getFiles() != null && !result.getFiles().isEmpty()) {
                fileId = result.getFiles().get(0).getId();
                Log.d(TAG, "File found in folder with ID: " + fileId);
            } else {
                Log.d(TAG, "No file found with the name " + DATABASE_FILE_NAME + " in folder ID: " + folderId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding file in folder: " + e.getMessage());
        }
        return fileId;
    }

    private String getOrCreateFolder() {
        String parentFolderId = "root";
        String folderId = null;
        try {
            String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" + DATABASE_FOLDER_NAME + "' and trashed = false";
            FileList result = googleDriveService.files().list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setFields("files(id, name)")
                    .execute();

            if (result.getFiles() != null && !result.getFiles().isEmpty()) {
                folderId = result.getFiles().get(0).getId();
                Log.d(TAG, "Folder found with ID: " + folderId);
            } else {
                File fileMetadata = new File();
                fileMetadata.setName(DATABASE_FOLDER_NAME);
                fileMetadata.setMimeType("application/vnd.google-apps.folder");
                fileMetadata.setParents(Collections.singletonList(parentFolderId));

                File folder = googleDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                folderId = folder.getId();
                Log.d(TAG, "Folder created with ID: " + folderId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getOrCreateFolder: " + e.getMessage());
        }
        return folderId;
    }

    private List<Record> SortRecords(List<Record> records) {
        return databaseService.getUniqueRecords(records);
    }

    private List<Record> parseJsonToDataList(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Record>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON to Data list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private java.io.File packDataIntoJson(List<Record> sortedRecords) {
        Gson gson = new Gson();

        String json = gson.toJson(sortedRecords);

        java.io.File jsonFile = new java.io.File(context.getCacheDir(), DATABASE_FILE_NAME);

        try (FileOutputStream fos = new FileOutputStream(jsonFile)) {
            fos.write(json.getBytes());
            Log.d(TAG, "Data saved to JSON file: " + jsonFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Error writing JSON file: " + e.getMessage());
        }
        return jsonFile;
    }

    public interface DownloadCallback {
        void onDownloadComplete(List<Record> records);
        void onError(String errorMessage);
    }
}

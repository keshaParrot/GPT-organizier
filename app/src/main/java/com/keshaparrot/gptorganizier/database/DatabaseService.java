package com.keshaparrot.gptorganizier.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.keshaparrot.gptorganizier.domain.Record;
import com.keshaparrot.gptorganizier.domain.TypeOfRecord;
import com.keshaparrot.gptorganizier.service.GoogleDriveSyncService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseService {
    private static volatile DatabaseService instance;
    private final RecordDao recordDao;
    private GoogleDriveSyncService googleDriveSyncService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Context context;

    private DatabaseService(Context context){
        AppDatabase database = AppDatabase.getInstance(context);
        recordDao = database.recordDao();
        this.context = context;
    }

    public static DatabaseService getInstance(Context context){
        if (instance == null) {
            synchronized (DatabaseService.class) {
                if (instance == null) {
                    instance = new DatabaseService(context);
                }
            }
        }
        return instance;
    }
    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseService is not initialized. Call getInstance(Context, GoogleAccountCredential) first.");
        }
        return instance;
    }
    private GoogleDriveSyncService getSyncService(Context context) {
        if (googleDriveSyncService == null) {
            googleDriveSyncService = GoogleDriveSyncService.getInstance(context);
        }
        return googleDriveSyncService;
    }
    public LiveData<List<Record>> getAll() {
        return recordDao.getAll();
    }

    public LiveData<List<Record>> getAllLinks() {
        return recordDao.getRecordsByType(TypeOfRecord.LINK.name());
    }

    public LiveData<List<Record>> getAllPrompts() {
        return recordDao.getRecordsByType(TypeOfRecord.PROMPT.name());
    }

    public LiveData<Record> getById(Long id) {
        return recordDao.getById(id);
    }

    public void insert(Record record) {
        executorService.execute(() -> {
            recordDao.insert(record);
            getSyncService(context).syncWithGoogleDrive();
        });
    }

    public void update(Record record) {
        executorService.execute(() -> {
            recordDao.update(record);
            getSyncService(context).syncWithGoogleDrive();
        });
    }

    public void delete(Long id) {
        executorService.execute(() -> {
            recordDao.deleteById(id);
            getSyncService(context).syncWithGoogleDrive();
        });
    }

    public void delete(Record record) {
        executorService.execute(() -> {
            recordDao.delete(record);
            getSyncService(context).syncWithGoogleDrive();
        });
    }
}

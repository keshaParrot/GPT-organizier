package com.keshaparrot.gptorganizier.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.keshaparrot.gptorganizier.domain.Record;
import com.keshaparrot.gptorganizier.domain.TypeOfRecord;
import com.keshaparrot.gptorganizier.service.GoogleDriveSyncService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton class that manages database operations for Record entities.
 * Provides methods to insert, update, delete, and retrieve records from the database.
 */
public class DatabaseService {
    private static volatile DatabaseService instance;
    private final RecordDao recordDao;
    private GoogleDriveSyncService googleDriveSyncService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Private constructor for DatabaseService.
     *
     * @param context the application context
     */
    private DatabaseService(Context context){
        AppDatabase database = AppDatabase.getInstance(context);
        recordDao = database.recordDao();
    }

    /**
     * Returns the singleton instance of DatabaseService.
     *
     * @param context the application context
     * @return the singleton instance of DatabaseService
     */
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
    /**
     * Retrieves the GoogleDriveSyncService instance.
     *
     * @param context the application context
     * @return the GoogleDriveSyncService instance
     */
    private GoogleDriveSyncService getSyncService(Context context) throws Exception {
        if (googleDriveSyncService == null) {
            googleDriveSyncService = GoogleDriveSyncService.getInstance(context);
        }
        return googleDriveSyncService;
    }

    /**
     * Retrieves all records from the database as LiveData.
     *
     * @return LiveData containing a list of all records
     */
    public LiveData<List<Record>> getAll() {
        return recordDao.getAll();
    }

    /**
     * Retrieves all records of type LINK from the database as LiveData.
     *
     * @return LiveData containing a list of all link records
     */
    public LiveData<List<Record>> getAllLinks() {
        return recordDao.getRecordsByType(TypeOfRecord.LINK.name());
    }

    /**
     * Retrieves all records of type PROMPT from the database as LiveData.
     *
     * @return LiveData containing a list of all prompt records
     */
    public LiveData<List<Record>> getAllPrompts() {
        return recordDao.getRecordsByType(TypeOfRecord.PROMPT.name());
    }

    /**
     * Retrieves a record by its ID as LiveData.
     *
     * @param id the ID of the record to retrieve
     * @return LiveData containing the record with the specified ID
     */
    public LiveData<Record> getById(Long id) {
        return recordDao.getById(id);
    }

    /**
     * Inserts a new record into the database.
     *
     * @param record the record to insert
     */
    public void insert(Record record) {
        executorService.execute(() -> recordDao.insert(record));
    }

    /**
     * Updates an existing record in the database.
     *
     * @param record the record to update
     */
    public void update(Record record) {
        executorService.execute(() -> recordDao.update(record));
    }

    /**
     * Deletes a record from the database by its ID.
     *
     * @param id the ID of the record to delete
     */
    public void delete(Long id) {
        executorService.execute(() -> recordDao.deleteById(id));
    }

    /**
     * Deletes a record from the database.
     *
     * @param record the record to delete
     */
    public void delete(Record record) {
        executorService.execute(() -> recordDao.delete(record));
    }

    /**
     * Merges new records with existing records to ensure uniqueness.
     *
     * @param newDataList the new list of records to merge
     * @return a List of unique records with their IDs
     */
    public List<Record> getUniqueRecords(List<Record> newDataList) {
        List<Record> localDataList = recordDao.getAllSync();
        Map<Long, Record> mergedDataMap = new HashMap<>();

        if (localDataList != null) {
            for (Record localData : localDataList) {
                mergedDataMap.put(localData.getId(), localData);
            }
        }

        for (Record newData : newDataList) {
            if (mergedDataMap.containsKey(newData.getId())) {
                Record existingData = mergedDataMap.get(newData.getId());

                if (newData.getUpdateTime().after(existingData.getUpdateTime())) {
                    mergedDataMap.put(newData.getId(), newData);
                }
            } else {
                mergedDataMap.put(newData.getId(), newData);
            }
        }

        return new ArrayList<>(mergedDataMap.values());
    }



    /**
     * Inserts multiple records into the database.
     *
     * @param records the list of records to insert
     */
    public void insertRecords(List<Record> records){
        recordDao.insertAll(records);
    }
}

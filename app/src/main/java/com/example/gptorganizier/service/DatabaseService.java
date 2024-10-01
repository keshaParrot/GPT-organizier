package com.example.gptorganizier.service;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gptorganizier.MainActivity;
import com.example.gptorganizier.domain.TypeOfRecord;
import com.example.gptorganizier.domain.Record;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class DatabaseService extends SQLiteOpenHelper {

    private static DatabaseService instance;
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECORDS = "records";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HEADER = "header";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CREATE_DATE = "createDate";
    private static final String COLUMN_UPDATE_TIME = "updateTime";
    private static final String COLUMN_TYPE = "type";

    private final GoogleDriveSyncService driveHelper;
    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static synchronized DatabaseService getInstance(Context context, GoogleAccountCredential credential) {
        if (instance == null) {
            instance = new DatabaseService(context, credential);
        }
        return instance;
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseService is not initialized. Call getInstance(Context, GoogleAccountCredential) first.");
        }
        return instance;
    }

    private DatabaseService(Context context, GoogleAccountCredential credential) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.driveHelper = GoogleDriveSyncService.getInstance(credential);
        this.context = context;

        if (credential.getSelectedAccount() != null) {
            downloadDatabaseFromDrive();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HEADER + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CREATE_DATE + " TEXT, " +
                COLUMN_UPDATE_TIME + " TEXT, " +
                COLUMN_TYPE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public void save(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADER, record.getHeader());
        values.put(COLUMN_CONTENT, record.getContent());
        values.put(COLUMN_DESCRIPTION, record.getDescription());
        values.put(COLUMN_CREATE_DATE, dateFormat.format(record.getCreateDate()));
        values.put(COLUMN_UPDATE_TIME, dateFormat.format(record.getUpdateTime()));
        values.put(COLUMN_TYPE, record.getType().name());

        db.insert(TABLE_RECORDS, null, values);
        synchronizeDatabase();
        ((MainActivity) context).updateAdapter();
        db.close();
    }
    public void update(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADER, record.getHeader());
        values.put(COLUMN_CONTENT, record.getContent());
        values.put(COLUMN_DESCRIPTION, record.getDescription());
        values.put(COLUMN_CREATE_DATE, dateFormat.format(record.getCreateDate()));
        values.put(COLUMN_UPDATE_TIME, dateFormat.format(record.getUpdateTime()));
        values.put(COLUMN_TYPE, record.getType().name());

        db.update(TABLE_RECORDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(record.getId())});
        synchronizeDatabase();
        ((MainActivity) context).updateAdapter();
        db.close();
    }
    public void delete(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        synchronizeDatabase();
        ((MainActivity) context).updateAdapter();
        db.close();
    }
    public Record getById(Long recordId) {
        Record record = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " WHERE " + COLUMN_TYPE + " = ? AND " + COLUMN_ID + " = ?", new String[]{"LINK", String.valueOf(recordId)});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int headerIndex = cursor.getColumnIndex(COLUMN_HEADER);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int createDateIndex = cursor.getColumnIndex(COLUMN_CREATE_DATE);
            int updateTimeIndex = cursor.getColumnIndex(COLUMN_UPDATE_TIME);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);

            if (idIndex != -1 && headerIndex != -1 && contentIndex != -1 && descriptionIndex != -1 &&
                    createDateIndex != -1 && updateTimeIndex != -1 && typeIndex != -1) {

                do {
                    Long id = cursor.getLong(idIndex);
                    String header = cursor.getString(headerIndex);
                    String content = cursor.getString(contentIndex);
                    String description = cursor.getString(descriptionIndex);
                    Date createDate = parseDate(cursor.getString(createDateIndex));
                    Date updateTime = parseDate(cursor.getString(updateTimeIndex));
                    TypeOfRecord type = TypeOfRecord.valueOf(cursor.getString(typeIndex));

                    record = new Record(id, header, content, description, createDate, updateTime, type);

                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "One or more columns not found in query result.");
            }
        }
        cursor.close();
        return record;
    }

    public List<Record> getAllLinks() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " WHERE " + COLUMN_TYPE + " = ?", new String[]{"LINK"});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int headerIndex = cursor.getColumnIndex(COLUMN_HEADER);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int createDateIndex = cursor.getColumnIndex(COLUMN_CREATE_DATE);
            int updateTimeIndex = cursor.getColumnIndex(COLUMN_UPDATE_TIME);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);

            if (idIndex != -1 && headerIndex != -1 && contentIndex != -1 && descriptionIndex != -1 &&
                    createDateIndex != -1 && updateTimeIndex != -1 && typeIndex != -1) {

                do {
                    Long id = cursor.getLong(idIndex);
                    String header = cursor.getString(headerIndex);
                    String content = cursor.getString(contentIndex);
                    String description = cursor.getString(descriptionIndex);
                    Date createDate = parseDate(cursor.getString(createDateIndex));
                    Date updateTime = parseDate(cursor.getString(updateTimeIndex));
                    TypeOfRecord type = TypeOfRecord.valueOf(cursor.getString(typeIndex));

                    Record record = new Record(id, header, content, description, createDate, updateTime, type);
                    records.add(record);
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "One or more columns not found in query result.");
            }
        }
        cursor.close();
        return records;
    }

    public int countObjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECORDS, null);
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }
    public List<Record> getAllPrompts() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " WHERE " + COLUMN_TYPE + " = ?", new String[]{"PROMPT"});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int headerIndex = cursor.getColumnIndex(COLUMN_HEADER);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int createDateIndex = cursor.getColumnIndex(COLUMN_CREATE_DATE);
            int updateTimeIndex = cursor.getColumnIndex(COLUMN_UPDATE_TIME);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);

            if (idIndex != -1 && headerIndex != -1 && contentIndex != -1 && descriptionIndex != -1 &&
                    createDateIndex != -1 && updateTimeIndex != -1 && typeIndex != -1) {

                do {
                    Long id = cursor.getLong(idIndex);
                    String header = cursor.getString(headerIndex);
                    String content = cursor.getString(contentIndex);
                    String description = cursor.getString(descriptionIndex);
                    Date createDate = parseDate(cursor.getString(createDateIndex));
                    Date updateTime = parseDate(cursor.getString(updateTimeIndex));
                    TypeOfRecord type = TypeOfRecord.valueOf(cursor.getString(typeIndex));

                    Record record = new Record(id, header, content, description, createDate, updateTime, type);
                    records.add(record);
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "One or more columns not found in query result.");
            }
        }
        cursor.close();
        return records;
    }
    private String getDatabaseHash(String databasePath) {
        try {
            File file = new File(databasePath);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            FileInputStream inputStream = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            inputStream.close();

            byte[] hashBytes = messageDigest.digest();
            StringBuilder hashString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hashString.append(String.format("%02x", hashByte));
            }

            return hashString.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error computing hash: " + e.getMessage());
            return null;
        }
    }

    public void synchronizeDatabase() {
        /*
            taking 2 database
            check hash of both db
            if different, check all records
            leave only which have last edit date
            upload
        
         */

        String localDatabasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        String localHash = getDatabaseHash(localDatabasePath);

        downloadDatabaseFromDrive();

        //TODO here we calculate hash 2 times on local db
        String remoteDatabasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        String remoteHash = getDatabaseHash(remoteDatabasePath);

        Log.e("REMOTE HASH",remoteHash);

        if (localHash != null && remoteHash != null) {
            if (!localHash.equals(remoteHash)) {
                Log.d(TAG, "Database hashes do not match. Synchronizing...");

                long localLastModified = new File(localDatabasePath).lastModified();
                long remoteLastModified = new File(remoteDatabasePath).lastModified();

                if (localLastModified > remoteLastModified) {
                    Log.d(TAG, "Uploading local database to Google Drive...");
                    uploadDatabaseToDrive();
                } else {
                    Log.d(TAG, "Downloading database from Google Drive...");
                    downloadDatabaseFromDrive();
                }
            } else {
                Log.d(TAG, "Database hashes match. No synchronization needed.");
            }
        } else {
            Log.e(TAG, "Unable to compute one or both database hashes.");
        }
    }
    public void uploadDatabaseToDrive() {
        String databasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        driveHelper.uploadDatabase(databasePath);
    }

    public void downloadDatabaseFromDrive() {
        String databasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        driveHelper.downloadDatabase(databasePath);
    }

    private Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error parsing date: " + e.getMessage());
            return null;
        }
    }


}


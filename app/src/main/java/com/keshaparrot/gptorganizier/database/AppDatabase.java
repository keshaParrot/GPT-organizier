package com.keshaparrot.gptorganizier.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.keshaparrot.gptorganizier.converters.DateConverter;
import com.keshaparrot.gptorganizier.converters.TypeOfRecordConverter;
import com.keshaparrot.gptorganizier.domain.Record;

@Database(entities = {Record.class}, version = 1)
@TypeConverters({DateConverter.class, TypeOfRecordConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract RecordDao recordDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "record_database")
                            .build();
                }
            }
        }
        return instance;
    }
}

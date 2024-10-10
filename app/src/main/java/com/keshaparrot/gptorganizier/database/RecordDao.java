package com.keshaparrot.gptorganizier.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.keshaparrot.gptorganizier.domain.Record;

import java.util.List;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM records")
    LiveData<List<Record>> getAll();

    @Query("SELECT * FROM records WHERE type = :type")
    LiveData<List<Record>> getRecordsByType(String type);

    @Query("SELECT * FROM records WHERE id = :id")
    LiveData<Record> getById(Long id);

    @Insert
    void insert(Record record);

    @Update
    void update(Record record);

    @Query("DELETE FROM records WHERE id = :id")
    void deleteById(Long id);

    @Delete
    void delete(Record record);
}


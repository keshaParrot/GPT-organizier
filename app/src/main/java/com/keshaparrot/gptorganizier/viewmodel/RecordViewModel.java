package com.keshaparrot.gptorganizier.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.keshaparrot.gptorganizier.database.DatabaseService;
import com.keshaparrot.gptorganizier.domain.Record;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class RecordViewModel extends ViewModel {
    private final DatabaseService databaseService;

    public RecordViewModel(Context context){
        this.databaseService = DatabaseService.getInstance(context);
    }

    public LiveData<List<Record>> getAllRecords() {
        return databaseService.getAll();
    }

    public LiveData<List<Record>> getAllLinks() {
        return databaseService.getAllLinks();
    }

    public LiveData<List<Record>> getAllPrompts() {
        return databaseService.getAllPrompts();
    }
    public LiveData<Record> getById(Long id) {
        return databaseService.getById(id);
    }
    public void insert(Record record) {
        databaseService.insert(record);
    }

    public void update(Record record) {
        databaseService.update(record);
    }

    public void delete(Long id) {
        databaseService.delete(id);
    }
}


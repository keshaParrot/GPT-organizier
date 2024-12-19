
package com.keshaparrot.gptorganizier.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RecordViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public RecordViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


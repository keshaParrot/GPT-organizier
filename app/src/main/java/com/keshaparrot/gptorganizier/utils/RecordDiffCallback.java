package com.keshaparrot.gptorganizier.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.keshaparrot.gptorganizier.domain.Record;

import java.util.List;

public class RecordDiffCallback extends DiffUtil.Callback {
    private final List<Record> oldList;
    private final List<Record> newList;

    public RecordDiffCallback(List<Record> oldList, List<Record> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

}

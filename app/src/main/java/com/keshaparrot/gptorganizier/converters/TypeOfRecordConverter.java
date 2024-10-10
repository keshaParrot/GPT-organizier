package com.keshaparrot.gptorganizier.converters;

import androidx.room.TypeConverter;

import com.keshaparrot.gptorganizier.domain.TypeOfRecord;

public class TypeOfRecordConverter {
    @TypeConverter
    public static String fromTypeOfRecord(TypeOfRecord type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static TypeOfRecord toTypeOfRecord(String type) {
        return type == null ? null : TypeOfRecord.valueOf(type);
    }
}


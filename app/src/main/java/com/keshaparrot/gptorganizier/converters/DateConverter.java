package com.keshaparrot.gptorganizier.converters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date longToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}

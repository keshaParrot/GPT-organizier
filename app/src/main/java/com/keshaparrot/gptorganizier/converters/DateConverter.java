package com.keshaparrot.gptorganizier.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Class to convert Date objects to Long timestamps and vice versa.
 * This is necessary for storing Date types in Room database.
 */
public class DateConverter {

    /**
     * Converts a Date object to a Long timestamp.
     *
     * @param date the Date object to convert
     * @return the corresponding timestamp as Long, or null if the date is null
     */
    @TypeConverter
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * Converts a Long timestamp to a Date object.
     *
     * @param timestamp the Long timestamp to convert
     * @return the corresponding Date object, or null if the timestamp is null
     */
    @TypeConverter
    public static Date longToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}

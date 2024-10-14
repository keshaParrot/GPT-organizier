package com.keshaparrot.gptorganizier.converters;

import androidx.room.TypeConverter;

import com.keshaparrot.gptorganizier.domain.TypeOfRecord;
/**
 * Converter for converting the TypeOfRecord enum to a String and vice versa.
 * Used by Room for storing and retrieving the record type in the database.
 */
public class TypeOfRecordConverter {

    /**
     * Converts a TypeOfRecord object to a String.
     *
     * @param type the TypeOfRecord object to convert
     * @return the string representation of the record type, or null if the type is null
     */
    @TypeConverter
    public static String fromTypeOfRecord(TypeOfRecord type) {
        return type == null ? null : type.name();
    }

    /**
     * Converts a String to a TypeOfRecord object.
     *
     * @param type the string representation of the record type
     * @return the TypeOfRecord object, or null if the type is null
     */
    @TypeConverter
    public static TypeOfRecord toTypeOfRecord(String type) {
        return type == null ? null : TypeOfRecord.valueOf(type);
    }
}


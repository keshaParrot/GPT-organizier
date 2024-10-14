package com.keshaparrot.gptorganizier.converters;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Date;

public class DateConverterTest extends TestCase {

    public void testDateToLong_NullDate() {
        Date date = null;
        Long result = DateConverter.dateToLong(date);
        assertNull(result);
    }

    public void testDateToLong_ValidDate() {
        Date date = new Date(1633072800000L);
        Long result = DateConverter.dateToLong(date);
        assertEquals(Long.valueOf(1633072800000L), result);
    }

    public void testLongToDate_NullTimestamp() {
        Long timestamp = null;
        Date result = DateConverter.longToDate(timestamp);
        assertNull(result);
    }

    public void testLongToDate_ValidTimestamp() {
        Long timestamp = 1633072800000L;
        Date result = DateConverter.longToDate(timestamp);
        assertEquals(new Date(1633072800000L), result);
    }
}
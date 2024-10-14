package com.keshaparrot.gptorganizier.converters;

import com.keshaparrot.gptorganizier.domain.TypeOfRecord;

import junit.framework.TestCase;

public class TypeOfRecordConverterTest extends TestCase {

    public void testFromTypeOfRecord() {
        assertEquals("LINK", TypeOfRecordConverter.fromTypeOfRecord(TypeOfRecord.LINK));
        assertEquals("PROMPT", TypeOfRecordConverter.fromTypeOfRecord(TypeOfRecord.PROMPT));
        assertNull(TypeOfRecordConverter.fromTypeOfRecord(null));
    }

    public void testToTypeOfRecord() {
        assertEquals(TypeOfRecord.LINK, TypeOfRecordConverter.toTypeOfRecord("LINK"));
        assertEquals(TypeOfRecord.PROMPT, TypeOfRecordConverter.toTypeOfRecord("PROMPT"));
        assertNull(TypeOfRecordConverter.toTypeOfRecord(null));
    }
}
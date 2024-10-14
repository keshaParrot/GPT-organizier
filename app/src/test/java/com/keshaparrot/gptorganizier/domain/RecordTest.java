package com.keshaparrot.gptorganizier.domain;

import junit.framework.TestCase;

public class RecordTest extends TestCase{

    private final Record record = new Record();

    public void testConvertType_Prompt() {
        String input = "PROMPT";

        TypeOfRecord result = record.convertType(input);

        assertEquals(TypeOfRecord.PROMPT, result);
    }

    public void testConvertType_Prompt_CaseInsensitive() {
        String input = "prompt";

        TypeOfRecord result = record.convertType(input);

        assertEquals(TypeOfRecord.PROMPT, result);
    }

    public void testConvertType_link_CaseInsensitive() {
        String input = "link";

        TypeOfRecord result = record.convertType(input);

        assertEquals(TypeOfRecord.LINK, result);
    }

    public void testConvertType_Link() {
        String input = "SOME_OTHER_TYPE";

        TypeOfRecord result = record.convertType(input);

        assertEquals(TypeOfRecord.LINK, result);
    }
}
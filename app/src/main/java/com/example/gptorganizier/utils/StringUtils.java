package com.example.gptorganizier.utils;

public class StringUtils {
    public static boolean isLongerThan(int charNum,String input) {
        return input == null || input.length() > charNum;
    }
}

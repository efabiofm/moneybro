package com.innovant.moneybro;

public class Utils {
    public static boolean isAlphanumeric(String str) {
        String numericPattern = ".*[0-9].*";
        String letterPattern = ".*[A-Za-z].*";
        return str.matches(numericPattern) && str.matches(letterPattern);
    }
}

package com.innovant.moneybro;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static boolean isAlphanumeric(String str) {
        String numericPattern = ".*[0-9].*";
        String letterPattern = ".*[A-Za-z].*";
        return str.matches(numericPattern) && str.matches(letterPattern);
    }

    public static String formatDate(Object dateObject) {
        Timestamp timestamp = (Timestamp) dateObject;
        Date fecha = timestamp.toDate();
        Locale locale = new Locale("es", "ES");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        return dateFormat.format(fecha);
    }
}

package com.example.pizzaapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    // Nên dùng Locale.getDefault() để nhất quán
    private static final String PATTERN = "dd/MM/yyyy HH:mm:ss";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN, Locale.getDefault());

    public static Date toDate(String st) throws ParseException {
        return sdf.parse(st);
    }

    public static String toString(Date date) {
        return sdf.format(date);
    }
}
package com.example.botcounter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MyCalendar {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    public static String getCurrentDate() {
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    public static Set<String> getAllDates(String startDate) {
        Set<String> result = new HashSet<>();
        try {
            Date start = new SimpleDateFormat(DATE_PATTERN).parse(startDate);
            Date end = new SimpleDateFormat(DATE_PATTERN).parse(getCurrentDate());
            start.setDate(start.getDate() + 1);
            while (!start.equals(end)) {
                result.add(new SimpleDateFormat(DATE_PATTERN).format(start));
                start.setDate(start.getDate() + 1);
            }
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
        return result;
    }
}

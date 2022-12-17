package com.example.botcounter.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCalendar {
    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}

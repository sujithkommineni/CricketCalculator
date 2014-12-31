package com.hydapps.cricketcalc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hrgn76 on 12/18/2014.
 */
public class DateUtils {
    public static String getDateTimeString(long mills) {
        DateFormat dateFormat= SimpleDateFormat.getDateTimeInstance();
        String s = dateFormat.format(new Date(mills));
        return s;
    }
}

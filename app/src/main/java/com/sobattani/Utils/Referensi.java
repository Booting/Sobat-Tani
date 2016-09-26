package com.sobattani.Utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Referensi {
    public static String url       = "http://dirgareksa.xyz/ws-sobattani";
    public static String PREF_NAME = "sobattani";

    public static String getDate(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);
            return sdf.format(calendar.getTime());
        } catch(Exception ex) {
            return "xx";
        }
    }

    public static long getRemainingDays(long lastUpdate) {
        Date dateLastUpdate     = new Date(lastUpdate);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        dateformat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        getSimpleDateFormatHours().setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long currentMillis = System.currentTimeMillis();
        Date dtLastUpdate  = null;
        try {
            dtLastUpdate = dateformat.parse(dateformat.format(dateLastUpdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long eventMillis   = dtLastUpdate.getTime();
        long diffMillis    = currentMillis - eventMillis;
        long remainingDays = diffMillis / 86400000;

        return remainingDays;
    }

    public static SimpleDateFormat getSimpleDateFormatHours() {
        SimpleDateFormat dateformatHours = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        return dateformatHours;
    }

    public static long getCurrentMillis() {
        long currentMillis = System.currentTimeMillis();
        return currentMillis;
    }

    public static String currencyFormater(Double value) {
        DecimalFormat myFormatter = new DecimalFormat("###,###,###");
        return myFormatter.format(value);
    }
}

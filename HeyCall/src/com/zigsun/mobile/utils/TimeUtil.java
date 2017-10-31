package com.zigsun.mobile.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Luo on 2015/6/26.
 */
public class TimeUtil {
    private static final long DAY = 86400000l;
    private static final long HOUR = 3600000L;
    private static final long WEAK = 604800000;
    private static final long YEAR = 31536000000L;
    private static final String TAG = TimeUtil.class.getSimpleName();

    public static String time(long milliseconds) {
        //时间戳转化为Sting或Date

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final long currentTimeMillis = System.currentTimeMillis();
        if (isAday(milliseconds, currentTimeMillis)) {
            format = new SimpleDateFormat("a hh:mm:ss", Locale.getDefault());
        } else if (inAWeak(currentTimeMillis, milliseconds)) {
            format = new SimpleDateFormat("EEEE HH:mm:ss", Locale.getDefault());
        } else if (inSameYear(currentTimeMillis, milliseconds)) {
            format = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
        }
        String d = format.format(milliseconds);
//        Date date = null;
//        try {
//            date = format.parse(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        System.out.println("Format To String(Date):" + d);
        return d;
//        System.out.println("Format To Date:" + date);

    }

    private static boolean isAday(long milliseconds, long currentTimeMillis) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(currentTimeMillis);
        final long l = ca.get(Calendar.HOUR_OF_DAY) * HOUR;
        return Math.abs(currentTimeMillis - milliseconds) < l;
    }

    public static boolean inSameYear(long currentTimeMillis, long milliseconds) {
//        Calendar ca = Calendar.getInstance();
//        ca.setTimeInMillis(currentTimeMillis);
//        final int cy = ca.get(Calendar.DAY_OF_YEAR);
//        ca.setTimeInMillis(milliseconds);
//        final int my = ca.get(Calendar.DAY_OF_YEAR);
//        Log.d(TAG, "cy: "+cy+ "  my: "+my);
        return Math.abs(currentTimeMillis - milliseconds) < YEAR;
//        return cy == my;
//        final long l = ca.get(Calendar.DAY_OF_MONTH) * DAY;
//        return l;
//        if(currentTimeMillis-milliseconds<l)
//            return true;
//        return false;
    }

    private static boolean inAWeak(long currentTimeMillis, long milliseconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(currentTimeMillis);
        final long l = ca.get(Calendar.DAY_OF_WEEK) * DAY;
        return Math.abs(currentTimeMillis - milliseconds) < l;
    }

    public static String getMinu(String min, String sec, long time) {
        int s = (int) (time / 1000);
        long m = s / 60;
        s %= 60;
        return String.format("%02d" + min + "%02d" + sec, m, s);

    }
}

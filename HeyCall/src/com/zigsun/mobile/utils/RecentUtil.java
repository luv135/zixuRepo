package com.zigsun.mobile.utils;

import android.util.Log;

import com.zigsun.mobile.module.CallStatus;
import com.zigsun.mobile.module.RecenListItem;

/**
 * Created by Luo on 2015/6/26.
 */
public class RecentUtil {
    private static final String TAG = RecentUtil.class.getSimpleName();


    public static int switchStatus(CallStatus status) {
        return CallStatus.switchStatus(status);

    }  public static CallStatus switchStatus(int status) {
        return CallStatus.switchStatus(status);

    }

    public static String switchPeople(String... name) {
        StringBuilder buffer = new StringBuilder();
        for (String a : name) {
            buffer.append(a).append(".");
        }
        final int length = buffer.length();
        return buffer.toString().substring(0, length - 1);
    }

    public static String[] switchPeople(String names) {
        final String[] split = names.split("\\.");
        return split;
    }

    public static String switchUlUserID(long... userIds) {
        StringBuilder buffer = new StringBuilder();
        for (long a : userIds) {
            buffer.append(a).append(".");
        }
        final int length = buffer.length();
        return buffer.toString().substring(0, length - 1);
    }

    public static long[] switchUlUserID(String userIds) {
        final String[] split = userIds.split("\\.");
        long[] users = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            users[i] = Long.parseLong(split[i]);
            Log.d(TAG, split[i] + "  ");
        }
        return users;
    }

    public static RecenListItem wrapItem(long begin, long end, CallStatus status, String[] people, long[] userIds) {
        final RecenListItem recenListItem = new RecenListItem(begin, end, switchPeople(people), switchStatus(status), switchUlUserID(userIds));
        Log.d(TAG,"wrapItem: "+recenListItem.toString());
        return recenListItem;
    }

    public static String[] switchNick(String nickName) {
        final String[] split = nickName.split("\\.");
        String[] users = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            users[i] = (split[i]);
            Log.d(TAG, split[i] + "  ");
        }
        return users;
    }
}

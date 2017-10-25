package com.zigsun.luo.projection.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Luo on 2015/5/13.
 */
public class Util {

    private static final String LOG_TAG = Util.class.getSimpleName();

    /*
        * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
        * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
        * http://code.google.com/p/android/issues/detail?id=9151
        */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
        return null;
    }

    public static String formatDateString(Context context, long time) {
        DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat
                .getTimeFormat(context);
        Date date = new Date(time);
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    public static Point getScreenResolution(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        return theScreenResolution;
    }

    // storage, G M K B
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static void setText(TextView textView, String text) {
        textView.setText(text);
    }

    /**
     * view.setVisibility(visibility)
     */
    public static void setVisibility(int visibility, View... views) {
        for (View v : views) {
            v.setVisibility(visibility);
        }
    }

    /**
     * view.startAnimation(animation)
     */
    public static void startAnimation(Animation animation, View... views) {
        for (View v : views) {
            v.startAnimation(animation);
        }
    }

    public static boolean isRootSystem() {
//        if (systemRootState == kSystemRootStateEnable) {
//            return true;
//        } else if (systemRootState == kSystemRootStateDisable) {
//            return false;
//        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (String kSuSearchPath : kSuSearchPaths) {
                f = new File(kSuSearchPath + "su");
                if (f.exists()) {
//                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
//        systemRootState = kSystemRootStateDisable;
        return false;
    }

    public static void setOnClickListener(View.OnClickListener l, View... views) {
        for (View v : views)
            v.setOnClickListener(l);
    }
}

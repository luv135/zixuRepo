package com.zigsun.luo.projection.fileexplore.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luo on 2015/5/12.
 * 文件工具
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();


    /**
     * @param path
     * @return
     */
    public static FileInfo getFileInfo(String path) {
        File file = new File(path);
        if (!file.exists())
            return null;
        return getFileInfo(file);
    }

    public static FileInfo getFileInfo(File child) {
        File lFile = child;//new File(filePath);
//        if (!lFile.exists())
//            return null;
        FileInfo lFileInfo = new FileInfo();
        lFileInfo.fileName = lFile.getName();
        lFileInfo.modifyDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = child.getPath();
        lFileInfo.fileSize = lFile.length();
        lFileInfo.Count = getChildCount(child, false);
        return lFileInfo;

    }

    /**
     * 得到文件夹下子文件数目
     *
     * @param containHide 是否包含隐藏文件
     */
    public static int getChildCount(File directory, boolean containHide) {
        File[] files;
        if ((files = directory.listFiles()) == null) return 0;
        int count = files.length;
        if (!containHide) {
            for (File aFile : files) {
                if (aFile.isHidden())
                    count--;
            }
        }
        return count;

    }


    /**
     * @param containHide 是否包含隐藏文件
     * @return an array of files or {@code null}.
     */
    public static List<FileInfo> getChildFiles(File directory, boolean containHide) {
//        if (!directory.isDirectory()) return null;
        File[] listFiles = directory.listFiles();
        if (listFiles == null) return null;
        List<FileInfo> files = new ArrayList<>();
        for (File aFile : listFiles) {
            if (!containHide && aFile.isHidden()) continue;
            files.add(getFileInfo(aFile));
        }
        return files;
    }


    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    /**
     * 获取文件名去掉扩展名
     *
     * @param filename
     * @return
     */
    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    public static String getMIMEType(String fileName) {
        String extension = getExtFromFilename(fileName);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getMimeTypeFromExtension(extension);
        return extension == null ? "*/*" : extension;
    }

    public static void viewFile(final Context context, final String filePath) {
        String type = getMIMEType(filePath);

        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
            /* 设置intent的file与MimeType */
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
            context.startActivity(intent);
        } else {
            // unknown MimeType
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);


            dialogBuilder.setTitle(R.string.dialog_select_type);

            CharSequence[] menuItemArray = new CharSequence[]{
                    context.getString(R.string.dialog_type_text),
                    context.getString(R.string.dialog_type_audio),
                    context.getString(R.string.dialog_type_video),
                    context.getString(R.string.dialog_type_image)};
            dialogBuilder.setItems(menuItemArray,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectType = "*/*";
                            switch (which) {
                                case 0:
                                    selectType = "text/plain";
                                    break;
                                case 1:
                                    selectType = "audio/*";
                                    break;
                                case 2:
                                    selectType = "video/*";
                                    break;
                                case 3:
                                    selectType = "image/*";
                                    break;
                            }
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(filePath)), selectType);
                            context.startActivity(intent);
                        }
                    });
            dialogBuilder.show();
        }
    }


    public static List<FileInfo> getChildFiles(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            List<FileInfo> files = new ArrayList<>();
            do {
                String filePath = cursor.getString(1);
                FileInfo fileInfo = getFileInfo(filePath);
                if (fileInfo != null) {
                    Log.d(TAG, "游标添加:" + fileInfo.toString());
                    files.add(fileInfo);
                }
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(TAG, "files size is " + files.size());
            return files;

        } else {
            Log.w(TAG, "cursor is null");
        }
        return null;
    }
}

package com.zigsun.luo.projection.fileexplore.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Luo on 2015/5/13.
 */
public class FileCategoryHelper {
    private static final String LOG_TAG = FileCategoryHelper.class.getSimpleName();

    public FileCategoryHelper(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;
    public static HashMap<FileCategory, Integer> categoryNames = new HashMap<FileCategory, Integer>();

    public enum FileCategory {
        Video, Picture, Music, Word, PPT, Excel, PDF, Apk, Other
    }

//
//    private static int[] images = new int[]{R.drawable.file_icon_video, R.drawable.file_icon_picture,
//            R.drawable.file_icon_mp3, R.drawable.file_icon_office, R.drawable.file_icon_office, R.drawable.file_icon_office,
//            R.drawable.file_icon_pdf};
//
    private static String APK_EXT = "apk";


    static {

    }

    public static FileCategory getCategoryFromPath(String path) {
        MediaFile.MediaFileType type = MediaFile.getFileType(path);
        if (type != null) {
            if (MediaFile.isVideoFileType(type.fileType)) return FileCategory.Video;
            if (MediaFile.isImageFileType(type.fileType)) return FileCategory.Picture;
        }
        int dotPosition = path.lastIndexOf('.');
        if (dotPosition < 0) {
            return FileCategory.Other;
        }

        String ext = path.substring(dotPosition + 1);
        if (ext.equalsIgnoreCase(APK_EXT)) {
            return FileCategory.Apk;
        }
        return FileCategory.Other;
    }

    private String buildSelectionByCategory(FileCategory cat) {
        String selection = null;
        switch (cat) {
            case Word:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'";
                break;

            case Excel:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.xls'";
                break;
            case PDF:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'";
                break;
            case PPT:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.ppt'";
                break;
            case Apk:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
                break;

            default:
                selection = null;
        }
        return selection;
    }

    public FileCategoryHelper() {
    }





    /**
     * 查询数据库
     *
     * @param fc
     * @param sort
     * @return
     */
    public Cursor query(FileCategory fc, FileSortHelper.SortMethod sort) {
        Uri uri = getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(fc);
        String sortOrder = buildSortOrder(sort);

        if (uri == null) {
            Log.e(LOG_TAG, "invalid uri, category:" + fc.name());
            return null;
        }

        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
        };
        Log.d(LOG_TAG, "类型:"+fc+"  "+selection);
        Cursor cursor = mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);

//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//
//                long id = cursor.getLong(0);
//                String filePath = cursor.getString(1);
//                Log.d(LOG_TAG, "mime=" + id + " fileName:" + filePath);
//            }
//            Log.d(LOG_TAG, "again~~~~~~~~~~~~~~~~~~~");
//            cursor.moveToFirst();
//            while (cursor.moveToNext()) {
//
//                long id = cursor.getLong(0);
//                String filePath = cursor.getString(1);
//                Log.d(LOG_TAG, "mime=" + id + " fileName:" + filePath);
//            }
//
//
////        fileInfo.fileSize = cursor.getLong(FileCategoryHelper.COLUMN_SIZE);
////        fileInfo.ModifiedDate = cursor.getLong(FileCategoryHelper.COLUMN_DATE);
//        } else {
//            Log.d(LOG_TAG, "cursor is null");
//        }
        return cursor;
    }

    private String buildSortOrder(FileSortHelper.SortMethod sort) {
        String sortOrder = null;
        switch (sort) {
            case name:
                sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = MediaStore.Files.FileColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc, " + MediaStore.Files.FileColumns.TITLE + " asc";
                break;
        }
        return sortOrder;
    }

    private Uri getContentUriByCategory(FileCategory cat) {
        Uri uri;
        String volumeName = "external";
        switch (cat) {
            case Word:
            case Excel:
            case PDF:
            case PPT:
                uri = MediaStore.Files.getContentUri(volumeName);
                break;
            case Music:
                uri = MediaStore.Audio.Media.getContentUri(volumeName);
                break;
            case Video:
                uri = MediaStore.Video.Media.getContentUri(volumeName);
                break;
            case Picture:
                uri = MediaStore.Images.Media.getContentUri(volumeName);
                break;
            default:
                uri = null;
        }
        return uri;
    }

}

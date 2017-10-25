package com.zigsun.luo.projection.fileexplore.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.helper.FileIconHelper;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.helper.FileItemHelper;
import com.zigsun.luo.projection.fileexplore.utils.FileUtils;

import java.util.HashMap;

/**
 * Created by Luo on 2015/5/18.
 */
public class CategoryAdapter extends CursorAdapter {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<Integer, FileInfo>();
    private FileItemHelper itemHelper;
    private FileIconHelper iconHelper;

    public CategoryAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        itemHelper = new FileItemHelper(context, inflater);
        iconHelper = new FileIconHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_directory_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FileInfo fileInfo = getItemByPosition(cursor.getPosition());
        Log.d(TAG,"bindView "+ fileInfo.toString());
        itemHelper.setCategoryItem(view, fileInfo, iconHelper);

//        cursor.getPosition()
    }

    private FileInfo getItemByPosition(int position) {
        FileInfo fileInfo = mFileNameList.get(position);
        if (fileInfo == null) {
            Cursor cursor = (Cursor) getItem(position);
            String path = cursor.getString(1);
            fileInfo = FileUtils.getFileInfo(path);
            Log.d(TAG,"添加:"+ fileInfo.toString());
            mFileNameList.put(position, fileInfo);
        } else{
            Log.d(TAG,"存在: "+ fileInfo.toString());
        }
        return fileInfo;

    }
}

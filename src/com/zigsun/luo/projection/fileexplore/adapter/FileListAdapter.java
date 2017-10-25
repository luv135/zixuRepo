package com.zigsun.luo.projection.fileexplore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zigsun.luo.projection.fileexplore.helper.FileIconHelper;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.helper.FileItemHelper;

import java.util.List;

/**
 * Created by Luo on 2015/5/12.
 * 本地文件ListView适配器
 */
public class FileListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<FileInfo> fileInfoList;
    private FileIconHelper iconHelper;
    private Context context;
    private FileItemHelper itemHelper;

    public FileListAdapter(Context context, List<FileInfo> fileInfoList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fileInfoList = fileInfoList;
        iconHelper = new FileIconHelper(context);
        itemHelper = new FileItemHelper(context, inflater);
    }

    @Override
    public int getCount() {
        return fileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfo fileInfo = fileInfoList.get(position);
        return itemHelper.setItem(convertView, fileInfo, iconHelper);
    }

}

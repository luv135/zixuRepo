package com.zigsun.luo.projection.fileexplore.base;

import android.content.Context;
import android.os.Environment;

import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.helper.FileSortHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Luo on 2015/5/12.
 * 文件操作处理
 */
public abstract class AbsFileInteraction implements IFileInteraction {
    private final static String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    protected Context mContext;
    protected String currentPath;
    protected List<FileInfo> fileList;    //currentPath 路径下的文件列表
    protected DataChangeListener changeListener;
    protected FileSortHelper sortHelper;      //排序类


    /**
     * //文件夹刷新
     */
    public interface DataChangeListener {
        void dataChange();
    }

    protected void notifyDataChange(){
        if(changeListener!=null){
        changeListener.dataChange();}
    }


    public AbsFileInteraction(Context mContext, AbsFileInteraction.DataChangeListener changeListener) {
        this.mContext = mContext;
        this.changeListener = changeListener;
        fileList = new ArrayList<>();
        sortHelper = new FileSortHelper();
    }

//    public void setCurrentPath(String currentPath) {
//        this.currentPath = currentPath;
//    }

//    @Override
//    public void refresh() {
//        refresh(currentPath);
//    }
//
//
//    @Override
//    public void refresh(int position) {
//        refresh(fileList.get(position).filePath);
//    }


    @Override
    public FileInfo getFileInfoByPosition(int position) {
        return fileList.get(position);
    }


    public void sortCurrentList(FileSortHelper sort) {
        Collections.sort(fileList, sort.getComparator());

    }

    public List<FileInfo> getFileList() {
        return fileList;
    }
}

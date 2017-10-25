package com.zigsun.luo.projection.fileexplore.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.base.AbsFileInteraction;
import com.zigsun.luo.projection.fileexplore.helper.FileCategoryHelper;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luo on 2015/5/19.
 */
public class FileCategoryModel extends AbsFileInteraction {


    private FileCategoryHelper helper;
    private String[] names;
    private int[] images;
    private static Map<Integer, FileCategoryHelper.FileCategory> categoryType = new HashMap<>();


    private CategoryListener listener;

    public interface CategoryListener extends DataChangeListener {
        void notifyDataChange(Cursor cursor);
    }

    public FileCategoryModel(Context mContext, CategoryListener changeListener) {
        super(mContext, changeListener);
        listener = changeListener;
        helper = new FileCategoryHelper(mContext);

        names = mContext.getResources().getStringArray(R.array.file_category_name);
        TypedArray ar = mContext.getResources().obtainTypedArray(R.array.file_category_pic);
        int len = ar.length();
        images = new int[len];
        for (int i = 0; i < len; i++) {
            images[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        categoryType.put(images[0], FileCategoryHelper.FileCategory.Music);
        categoryType.put(images[1], FileCategoryHelper.FileCategory.Video);
        categoryType.put(images[2], FileCategoryHelper.FileCategory.Picture);
        categoryType.put(images[3], FileCategoryHelper.FileCategory.Word);
        categoryType.put(images[4], FileCategoryHelper.FileCategory.Excel);
        categoryType.put(images[5], FileCategoryHelper.FileCategory.PDF);
        categoryType.put(images[6], FileCategoryHelper.FileCategory.PPT);

    }

//    @Override
//    public void refresh() {
//        home();
//    }

    private void home() {
        fileList.clear();
        for (int i = 0; i < names.length; i++) {
            FileInfo info = new FileInfo();
            info.fileName = names[i];
            info.image = images[i];
            info.IsDir = true;  //assume the true is home page.
            fileList.add(info);

        }
        notifyDataChange();
    }

//    @Override
//    public void refresh(int position) {
//        FileInfo info = fileList.get(position);
//        refresh(info);
//    }

    /**
     *
     * @param info null then go home page
     */
    @Override
    public void refresh(FileInfo info) {
        if (info == null) {
            home();
        } else {
            if (info.IsDir) { //home page
                FileCategoryHelper.FileCategory fileCategory = categoryType.get(info.image);
                Cursor cursor = helper.query(fileCategory, sortHelper.getSortMethod());
                fileList.clear();
                List<FileInfo> childFiles = FileUtils.getChildFiles(cursor);
                if (childFiles != null) {
                    fileList.addAll(childFiles);
                }
                listener.notifyDataChange(cursor);
            } else {
                FileUtils.viewFile(mContext, info.filePath);
            }
        }
    }

//    @Override
//    public void refresh(String path) {
//
//    }

}

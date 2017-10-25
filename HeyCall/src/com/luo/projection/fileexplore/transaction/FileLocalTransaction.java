package com.zigsun.luo.projection.fileexplore.transaction;

import android.content.Context;
import android.util.Log;

import com.zigsun.luo.projection.fileexplore.base.AbsFileInteraction;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Luo on 2015/5/12.
 * 文件操作处理
 */
public class FileLocalTransaction extends AbsFileInteraction {

    private static final String TAG = FileLocalTransaction.class.getSimpleName();

    public FileLocalTransaction(Context mContext, AbsFileInteraction.DataChangeListener changeListener) {
        super(mContext, changeListener);
    }

    private void refresh(String path) {
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            FileUtils.viewFile(mContext, path);
        } else {
            fileList.clear();
            if (file.exists()) {
                currentPath = path;
                List<FileInfo> files = FileUtils.getChildFiles(file, false);
                if (files != null) {
                    fileList.addAll(files);
                    sortCurrentList(sortHelper);
                }
            }
            notifyDataChange();
        }

//
//        if (!file.exists()) {
//            Log.d(TAG, "file not exists");
//        return;
//        }
//        if (file.isDirectory()) {
//            currentPath = path;
//            fileList.clear();
//            List<FileInfo> files = FileUtils.getChildFiles(file, false);
//            if (files != null) {
//                fileList.addAll(files);
//                sortCurrentList(sortHelper);
//            }
//            notifyDataChange();
//        } else {
//            FileUtils.viewFile(mContext, path);
//        }
//        }
    }

    /**
     * <b>Note:</b> the FileInfo's filePath can't be null
     *
     * @param file
     */
    @Override
    public void refresh(FileInfo file) {
        if (file.filePath == null) {
            Log.e(TAG, "the path is null");
            return;
        }
        Log.d(TAG, "refresh " + file.filePath);
        refresh(file.filePath);
    }

}

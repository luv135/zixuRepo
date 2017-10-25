package com.zigsun.luo.projection.fileexplore.ui;


import android.util.Log;

import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.fileexplore.utils.FileUtils;

import java.io.File;

/**
 * Environment.getRootDirectory().getParentFile().getPath()/mnt/
 */
public class FileUsbFragment extends FileLocalFragment {
    private static final String TAG = FileUsbFragment.class.getSimpleName();

    public FileUsbFragment() {
        Log.d(TAG, "FileUsbFragment()");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     *
     */
    @Override
    public void mount() {
        setRootPath(getRootPath());
        super.mount();
    }

    @Override
    protected String getRootPath() {
        String path = "";
        File[] files = new File("/mnt").listFiles();
        if (files != null) {
            for (File f : files) {
                final FileInfo info = FileUtils.getFileInfo(f);
                Log.d(TAG, "mnt path: " + f.getPath());
                if (!f.getAbsolutePath().toLowerCase().contains("sdcard") && info.IsDir && info.Count != 0) {
                    path = f.getAbsolutePath();
                    break;
                }
            }
        }
        return path;
//        return Environment.getRootDirectory().getParentFile().getPath()+File.separator+"mnt"+File.separator+"udisk";
    }


}

package com.zigsun.luo.projection.fileexplore.base;


import com.zigsun.luo.projection.fileexplore.helper.FileInfo;

/**
 * Created by Luo on 2015/5/12.
 *
 */
public interface IFileInteraction {

//    void refresh();
//
//    void refresh(String path);
//
//    void refresh(int position);

    void refresh(FileInfo file);

    FileInfo getFileInfoByPosition(int position);

}

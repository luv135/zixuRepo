package com.zigsun.luo.projection.fileexplore.helper;

/**
 * 文件信息
 * 文件名
 * 路径
 * 是否为文件夹
 * 修改日期
 * ...
 */
public class FileInfo {

    public String fileName;

    public int image;

    public String filePath;

    public long fileSize;

    public boolean IsDir;

    public long modifyDate;

    public int Count;

    public FileInfo() {
    }

    public FileInfo(FileInfo fileInfo) {
        this.fileName = fileInfo.fileName;
        this.image = fileInfo.image;
        this.filePath = fileInfo.filePath;
        this.fileSize = fileInfo.fileSize;
        this.IsDir = fileInfo.IsDir;
        this.modifyDate = fileInfo.modifyDate;
        this.Count = fileInfo.Count;

    }

    public FileInfo(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "FileInfo: name:" + fileName + " filePath:" + filePath;
    }
}

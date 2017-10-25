package com.zigsun.luo.projection.fileexplore.helper;

import android.content.Context;
import android.widget.ImageView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.utils.FileUtils;

import java.util.HashMap;

/**
 * Created by Luo on 2015/5/13.
 */
public class FileIconHelper implements FileIconLoader.IconLoadFinishListener {
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

    static {
        addItem(new String[] {
                "mp3"
        }, R.drawable.file_icon_mp3);
        addItem(new String[] {
                "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[] {
                "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[] {
                "mid"
        }, R.drawable.file_icon_mid);
        addItem(new String[] {
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf"
        }, R.drawable.file_icon_video);
        addItem(new String[] {
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.file_icon_picture);
        addItem(new String[] {
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_icon_txt);
        addItem(new String[] {
                "doc", "ppt", "docx", "pptx", "xsl", "xslx",
        }, R.drawable.file_icon_office);
        addItem(new String[] {
                "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[] {
                "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[] {
                "mtz"
        }, R.drawable.file_icon_theme);
        addItem(new String[] {
                "rar"
        }, R.drawable.file_icon_rar);
    }

    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    public static int getFileIcon(String ext) {
        Integer i = fileExtToIcons.get(ext.toLowerCase());
        if (i != null) {
            return i.intValue();
        } else {
            return R.drawable.file_icon_default;
        }

    }
    private FileIconLoader mIconLoader;

    public FileIconHelper(Context context) {
        mIconLoader = new FileIconLoader(context, this);

    }

    public void setIcon(ImageView imageView, String filePath) {
        String ext = FileUtils.getExtFromFilename(filePath);
        int fileIcon = getFileIcon(ext);
        imageView.setImageResource(fileIcon);
        FileCategoryHelper.FileCategory fc = FileCategoryHelper.getCategoryFromPath(filePath);
        boolean set = false;
        switch (fc) {
            case Apk:
                set = mIconLoader.loadIcon(imageView, filePath,  fc);
                break;
            case Picture:
            case Video:
                set = mIconLoader.loadIcon(imageView, filePath,  fc);
                if (!set){
                    imageView.setImageResource(fc == FileCategoryHelper.FileCategory.Picture ? R.drawable.file_icon_picture
                            : R.drawable.file_icon_video);
                    set = true;
                }
                break;
            default:
                set = true;
                break;
        }
        if (!set)
            imageView.setImageResource(R.drawable.file_icon_default);
    }

    @Override
    public void onIconLoadFinished(ImageView view) {

    }
}

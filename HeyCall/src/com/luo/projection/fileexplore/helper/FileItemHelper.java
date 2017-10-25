package com.zigsun.luo.projection.fileexplore.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.utils.Util;

/**
 * Created by Luo on 2015/5/18.
 */
public final class FileItemHelper {

    private Context context;
    private LayoutInflater inflater;

    public FileItemHelper(Context context, LayoutInflater inflater) {
        this.context = context;
        this.inflater = inflater;
    }

    public View setItem(View view, FileInfo fileInfo, FileIconHelper iconHelper) {
        FileItemHelper.Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_directory_item, null);
            holder = new FileItemHelper.Holder(view);
            view.setTag(holder);
        } else {
            holder = (FileItemHelper.Holder) view.getTag();
        }
        setInfo(fileInfo, iconHelper, holder);
        return view;
    }

    private void setInfo(FileInfo fileInfo, FileIconHelper iconHelper, Holder holder) {
        Util.setText(holder.name, fileInfo.fileName);
        Util.setText(holder.modifyDate, Util.formatDateString(context, fileInfo.modifyDate));
        Util.setText(holder.description,!fileInfo.IsDir ? Util.convertStorage(fileInfo.fileSize) : "("+fileInfo.Count+")");
        if (fileInfo.IsDir)
            holder.icon.setImageResource(R.drawable.directory_default);
        else {
            iconHelper.setIcon(holder.icon, fileInfo.filePath);
        }
    }

    public void setCategoryItem(View view, FileInfo fileInfo, FileIconHelper iconHelper) {
        FileItemHelper.Holder holder;
        holder = new FileItemHelper.Holder(view);
        setInfo(fileInfo, iconHelper, holder);
    }

    private  class Holder {
        public ImageView icon;
        public TextView name;
        public TextView description;
        public TextView modifyDate;

        public Holder(View root) {
            icon = (ImageView) root.findViewById(R.id.file_image);
            name = (TextView) root.findViewById(R.id.tv_name);
            modifyDate = (TextView) root.findViewById(R.id.tv_modify_date);
            description = (TextView) root.findViewById(R.id.tv_description);
        }
    }

}

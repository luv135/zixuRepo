package com.zigsun.luo.projection.fileexplore.event;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.zigsun.luo.projection.fileexplore.base.IFileInteraction;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.ProjectionActivity;

/**
 * Created by Luo on 2015/5/19.
 */
public class FileCategoryEvent implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ProjectionActivity.BackPressListener {


    private static final String TAG = FileCategoryEvent.class.getSimpleName();

    public FileCategoryEvent(IFileInteraction interaction) {
        this.interaction = interaction;
    }

    private IFileInteraction interaction;

    private boolean needGoHome;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo info = interaction.getFileInfoByPosition(position);
        if (info.IsDir) {
            needGoHome = true;
        }
        interaction.refresh(info);
    }


    public void goHome() {
        Log.d(TAG, "goHome()");
        interaction.refresh(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        FileInfo fileInfo = interaction.getFileInfoByPosition(position);
//        File file = new File(fileInfo.filePath);
//        file.delete();
//        interaction.refresh();
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (needGoHome) {
            goHome();
            needGoHome = false;
            return true;
        }
        return false;
    }
}

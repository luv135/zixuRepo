package com.zigsun.luo.projection.fileexplore.event;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zigsun.luo.projection.fileexplore.base.IFileInteraction;
import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.ProjectionActivity;

import java.io.File;
import java.util.Stack;

/**
 * Created by Luo on 2015/5/12.
 * 列表事件处理
 */
public class FileListEvent implements AdapterView.OnItemClickListener, ProjectionActivity.BackPressListener, AbsListView.OnScrollListener {

    private static final String TAG = FileListEvent.class.getSimpleName();
    private static final int HIDE_ITEM_LIMIT = 5;
    //    private ListPositionInfo listCurrentPosition;
    private Stack<FileState> fileStack;

    private IFileInteraction interaction;
    public int position;
    public int top;
    private String rootPath;

    public FileListEvent(IFileInteraction interaction) {
        fileStack = new Stack<>();
        this.interaction = interaction;
//        listCurrentPosition = new ListPositionInfo();
    }

    public void listChange(ListView listView) {
        listView.setSelectionFromTop(position, top);
    }

    @Override
    public boolean onBackPressed() {
        return backDirectory();
    }

    public void onStop() {

    }

    /**
     * 重置根节点,则必然清空历史栈
     *
     * @param rootPath
     */
    public void setRootPath(String rootPath) {
        Log.d(TAG, "setRootPath");
        if (this.rootPath == null || !this.rootPath.equals(rootPath)) {
            this.rootPath = rootPath;
            fileStack.clear();
            Log.d(TAG, "setRootPath is different and reset");
        }

    }

    public void clearHistory() {
        fileStack.clear();

    }


    @Override
    public void onScrollStateChanged(AbsListView parent, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            this.position = parent.getFirstVisiblePosition();
            this.top = parent.getChildAt(0).getTop();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * item 的路径,以及listView位置
     * 后续返回上一层现场恢复
     */
    private class FileState {
        private final String path;
        //        ListPositionInfo position;
        int itemPosition;
        int top;

        public FileState(String path, int itemPosition, int top) {
            this.itemPosition = itemPosition;
            this.path = path;
            this.top = top;
        }
//
//        public FileState(String path, ListPositionInfo position) {
//            this.path = path;
//            this.position = position;
//        }
    }

//    private class ListPositionInfo {
//        int position;
//        int top;
//
//
//        public ListPositionInfo(int position, int top) {
//            this.position = position;
//            this.top = top;
//        }
//
//        @Override
//        public String toString() {
//            return "position x= " + position + "  y= " + top;
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo fileInfo = interaction.getFileInfoByPosition(position);

        if (fileInfo.IsDir) {
            this.position = parent.getFirstVisiblePosition();
            this.top = parent.getChildAt(0).getTop();
            Log.d(TAG, "push " + fileInfo.filePath);
            fileStack.push(new FileState(fileInfo.filePath, this.position, this.top));
        }
        this.position = 0;
        this.top = 0;
        interaction.refresh(fileInfo);
    }


    public void refreshCurrentPath() {
        String path = fileStack.empty() ? rootPath : fileStack.peek().path;
        Log.d(TAG, "refreshCurrentPath: " + path);

        interaction.refresh(new FileInfo(path));
    }


    private ListScrollListener listScrollListener;

    public interface ListScrollListener {
        void show();

        void hider();
    }

    /**
     * @return {@code true} if parent directory exist then go back, {@code false} otherwise ;
     */
    private boolean backDirectory() {
        if (fileStack.isEmpty()) return false;
        FileState file = fileStack.pop();

//        Log.d(TAG, "pop " + listCurrentPosition.toString());
        Log.d(TAG, "backDirectory " + file.path);

        position = file.itemPosition;
        top = file.top;
        String parentPath = new File(file.path).getParentFile().getPath();
        interaction.refresh(new FileInfo(parentPath));
        return true;
    }


}

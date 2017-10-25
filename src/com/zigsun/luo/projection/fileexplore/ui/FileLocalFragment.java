package com.zigsun.luo.projection.fileexplore.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zigsun.luo.projection.fileexplore.base.MediaMountReceiver;
import com.zigsun.luo.projection.ProjectionActivity;
import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.adapter.FileListAdapter;
import com.zigsun.luo.projection.fileexplore.model.FileLocalModel;
import com.zigsun.luo.projection.fileexplore.event.FileListEvent;


/**
 * 本地文件
 */
public class FileLocalFragment extends Fragment implements ProjectionActivity.BackPressListener,
        FileLocalModel.DataChangeListener, MediaMountReceiver.IMediaState {


    private static final String TAG = FileLocalFragment.class.getSimpleName();
    //    private static final String EXTERNAL_STORAGE = Environment.getRootDirectory().getParentFile().getPath();
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private FileListAdapter listAdapter;
    private FileLocalModel listCore;
    private FileListEvent listEvent;
    private ListView listView;

    public FileLocalFragment() {
        Log.d(TAG, "FileLocalFragment()");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list_view);

        listView.setEmptyView(view.findViewById(R.id.file_local_list_empty));

        listCore = new FileLocalModel(getActivity(), this);

        listAdapter = new FileListAdapter(getActivity(), listCore.getFileList());


        listView.setAdapter(listAdapter);

        listEvent = new FileListEvent(listCore);

        listView.setOnItemClickListener(listEvent);
        listView.setOnScrollListener(listEvent);
        view.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listEvent.refreshCurrentPath();
            }
        });
        listEvent.setRootPath(getRootPath());
        listEvent.refreshCurrentPath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_local, container, false);
    }

    protected String getRootPath() {
        return SDCARD_PATH;
    }

    protected void setRootPath(String rootPath) {
        listEvent.setRootPath(rootPath);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MediaMountReceiver.Helper.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MediaMountReceiver.Helper.unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        listEvent.onStop();
        Log.d(TAG, "onStop()");

    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed");
        return listEvent.onBackPressed();
    }

    @Override
    public void dataChange() {
        listAdapter.notifyDataSetChanged();
        listEvent.listChange(listView);
    }

    @Override
    public void unMount() {
        listEvent.refreshCurrentPath();
    }

    @Override
    public void mount() {
        listEvent.refreshCurrentPath();
    }
}

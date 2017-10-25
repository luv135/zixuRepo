package com.zigsun.luo.projection.fileexplore.ui;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.ProjectionActivity;
import com.zigsun.luo.projection.fileexplore.adapter.CategoryHomeAdapter;
import com.zigsun.luo.projection.fileexplore.adapter.FileListAdapter;
import com.zigsun.luo.projection.fileexplore.model.FileCategoryModel;
import com.zigsun.luo.projection.fileexplore.event.FileCategoryEvent;


/**
 * A simple {@link Fragment} subclass.
 */
public class FileCategoryFragment extends Fragment implements FileCategoryModel.CategoryListener, ProjectionActivity.BackPressListener {


    private static final String TAG = FileCategoryFragment.class.getSimpleName();
    private FileCategoryEvent categoryEvent;
    private FileCategoryModel categoryCore;
    private CategoryHomeAdapter homeAdapter;
    private FileListAdapter adapter;
    private ListView listView;
    private GridView gridView;
    private View listEmptyView;

    public FileCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        gridView = (GridView) view.findViewById(R.id.gv_home);
        listView = (ListView) view.findViewById(R.id.lv_detail);
        categoryCore = new FileCategoryModel(getActivity(), this);
        categoryEvent = new FileCategoryEvent(categoryCore);
        homeAdapter = new CategoryHomeAdapter(getActivity(), categoryCore.getFileList());
        gridView.setAdapter(homeAdapter);
        adapter = new FileListAdapter(getActivity(), categoryCore.getFileList());

        listEmptyView = view.findViewById(R.id.list_empty);
        listView.setEmptyView(listEmptyView);
        listView.setAdapter(adapter);
        gridView.setOnItemClickListener(categoryEvent);
        listView.setOnItemClickListener(categoryEvent);
        listView.setOnItemLongClickListener(categoryEvent);

        categoryEvent.goHome();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_file_category, container, false);
    }


    @Override
    public void dataChange() {
        Log.d(TAG, "dataChange");
        listView.setVisibility(View.GONE);
        listEmptyView.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataChange(Cursor cursor) {
        Log.d(TAG, "notify cursor");
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onBackPressed() {
        return categoryEvent.onBackPressed();
    }
}

package com.zigsun.mobile.ui.child;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.zxing.aztec.detector.Detector;
import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.RecentAdapter;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.model.RecentModel;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.module.DialingUserInfo;
import com.zigsun.mobile.module.RecentNickItem;
import com.zigsun.mobile.module.UserInfoStatus;
import com.zigsun.mobile.observers.RecentObserver;
import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;
import com.zigsun.mobile.ui.meeting.DialingActivity;
import com.zigsun.mobile.utils.RecentUtil;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.ui.contact.SortModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends AbsBaseViewPagerFragment implements RecentObserver.RecentDateChangeListener {


    private static final String TAG = RecentFragment.class.getSimpleName();
    @InjectView(R.id.recentListView)
    ListView recentListView;
    private RecentModel module;
    private RecentAdapter adapter;
    private RecentFragmentEvent event;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.abc_fragment_recent, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        RecentObserver.getInstance().registerObserver(this);
        event = new RecentFragmentEvent();
        module = new RecentModel();

        adapter = new RecentAdapter(activity, module.getHistroyCall());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RecentObserver.getInstance().unRegisterObserver(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(recentListView);
        recentListView.setAdapter(adapter);
        recentListView.setOnItemClickListener(event);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        menu.setHeaderTitle("position: " + info.position);
        getActivity().getMenuInflater().inflate(R.menu.recent_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                module.deleteRecord(menuInfo.position);
//                Toast.makeText(getActivity(), "position is " + menuInfo.position, Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public int getPageTitle() {
        return R.string.recent_fragment_title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void recentDateChange() {
        module.getHistroyCall();
        adapter.notifyDataSetChanged();
        Log.d(TAG, "recentDateChange");
    }

    private class RecentFragmentEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final RecentNickItem item = (RecentNickItem) adapter.getItem(position);
            final long[] longs = RecentUtil.switchUlUserID(item.getUlUserID());
            final String[] names = RecentUtil.switchPeople(item.getPeople());
            final String[] nickNames = RecentUtil.switchNick(item.getNickName());
            final List<UserInfo> wrap = Utils.wrap(names, longs, nickNames);
            final List<SortModel> contactsList = new ContactsModel().getContactsList(null);
            updateUserStatus(contactsList, wrap);
            EMeetingApplication.getUserInfos().clear();
            EMeetingApplication.addUserInfo(wrap);
            startActivity(new Intent(getActivity(), DialingActivity.class));
        }

        private void updateUserStatus(List<SortModel> contactsList, List<UserInfo> wrap) {
            for (UserInfo a : wrap) {
                final ContactItem sortModel = (ContactItem) Utils.find(contactsList, a.getUlUserID());
                assert sortModel != null;
                Log.d(TAG, "updateUserStatus: " + sortModel.getNickName() + " - " + sortModel.getInfoStatus());
                final UserInfoStatus infoStatus = sortModel.getInfoStatus();
                final UserInfoStatus status = infoStatus ;//== UserInfoStatus.InMeeting ? UserInfoStatus.Online : infoStatus;
                ((DialingUserInfo) a).setStatus(status);
            }
        }
    }
}

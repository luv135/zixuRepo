package com.zigsun.mobile.ui.child;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.AboutActivity;
import com.zigsun.mobile.R;
import com.zigsun.mobile.ui.MainActivity;
import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;
import com.zigsun.mobile.ui.personal.information.UserInformationActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends AbsBaseViewPagerFragment {


    private static final String TAG = MeFragment.class.getSimpleName();
    @InjectView(R.id.meLayout)
    RelativeLayout meLayout;
    @InjectView(R.id.userNameText)
    TextView userNameText;
    @InjectView(R.id.aboutLayout)
    LinearLayout aboutLayout;

    public MeFragment() {
        // Required empty public constructor
    }

    private MeEvent event;

    private class MeEvent implements AdapterView.OnItemClickListener, View.OnClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(getActivity(),"hehe",Toast.LENGTH_SHORT).show();

            Intent intent;
            switch (v.getId()) {
                case R.id.aboutLayout:
                    intent = new Intent(getActivity(), AboutActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.meLayout:




                    intent = new Intent(getActivity(), UserInformationActivity.class);
                    getActivity().startActivityForResult(intent, MainActivity.EXIST);
                    break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult" + requestCode + " resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.abc_fragment_me, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        event = new MeEvent();
        meLayout.setOnClickListener(event);
        aboutLayout.setOnClickListener(event);
        userNameText.setText(EMeetingApplication.getUserInfo().getStrUserName());
    }


    @Override
    public int getPageTitle() {
        return R.string.me_fragment_title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

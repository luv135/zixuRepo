package com.zigsun.mobile.ui.child;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zigsun.EMeetingApplication;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.CallAdapter;
import com.zigsun.mobile.interfaces.LayoutChange;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;
import com.zigsun.mobile.ui.base.AnimationListener;
import com.zigsun.mobile.ui.base.CallHeadLayout;
import com.zigsun.mobile.ui.base.Keyboard;
import com.zigsun.mobile.ui.meeting.DialingActivity;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.ui.contact.SortModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
//

/**
 * A simple {@link Fragment} subclass.
 */
public class CallFragment extends AbsBaseViewPagerFragment {


    private static final String TAG = CallFragment.class.getSimpleName();
    @InjectView(R.id.searchListView)
    ListView searchListView;
    @InjectView(R.id.showKeyboardButton)
    Button showKeyboardButton;
    @InjectView(R.id.deleteButton)
    Button deleteButton;
    @InjectView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @InjectView(R.id.keyboard)
    Keyboard keyboard;
    private CallEvent event;
    private LayoutChange layoutChange;
    private CallModel model;
    private CallAdapter adapter;

    private class CallModel {
        private final SoundPool sp;
        private final int music;
        private ContactsModel model;

        public CallModel(Context context) {
            model = new ContactsModel();
            sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            music = sp.load(context, R.raw.test_key, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        }

        public String number = "";
        private boolean readToCall = false;

        public void keyDown(int code, String value) {
            sp.play(music, 1, 1, 0, 0, 1);

            switch (code) {
                case Keyboard.CODE_ADD:
                    readToCall = false;
                    break;
                case Keyboard.CODE_CALL:
                case Keyboard.CODE_CALL_VIDEO:
                    if (readToCall) {
                        readToCall = false;
                        Log.d(TAG, "readToCall");
                        call(adapter.getItem(0));
                        return;
                    }
                    if (adapter.getCount() > 0) {
                        number = adapter.getItem(0).getName();
                        readToCall = true;
                    }
                    break;
                case Keyboard.CODE_DELETE:
                    readToCall = false;
                    if (!TextUtils.isEmpty(number)) {
                        int end = number.length() - 1;
                        end = end < 0 ? 0 : end;
                        number = TextUtils.substring(number, 0, end);
                    }
                    break;
                default:
                    readToCall = false;
                    number += value;
            }

            updateHeade();
            Log.d(TAG, "number = " + number);

        }

        private void updateHeade() {
            Log.d(TAG, "updateHeade number=" + number);
            getContactsList(number);
            adapter.notifyDataSetChanged(number);
            if (TextUtils.isEmpty(number)) {
                if (headerText != null) {
                    layoutChange.removeView(headerText);
                    headerText = null;
                }
            } else {
                if (headerText == null) {
                    headerText = new CallHeadLayout(getActivity());
                    headerText.setDeleteOnClickListener(event);
                    headerText.setDeleteOnLongClickListener(event);
                    layoutChange.addView(headerText);
                }
            }
            if (headerText != null) {
                headerText.setText(number);
            }
        }

        public List<SortModel> getContactsList(String name) {
            if (TextUtils.isEmpty(number)) {
                return model.getEmptyItems();
            }
            return model.getContactsList(name);
        }

        public void call(SortModel canCallItem) {
            number = "";
            updateHeade();
            Log.d(TAG, "call");
            final Intent intent = new Intent(getActivity(), DialingActivity.class);
            EMeetingApplication.getUserInfos().clear();
            EMeetingApplication.addUserInfo(Utils.wrap(canCallItem));
            startActivity(intent);
        }

        public List<SortModel> getEmptyItems() {
            return model.getEmptyItems();
        }

        public void clearNumber() {
            number = "";
            updateHeade();
        }
    }

    private CallHeadLayout headerText;

    private class CallEvent implements Keyboard.KeyboardListener, AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener, View.OnLongClickListener {

        @Override
        public void keyDown(int code, String value) {
            model.keyDown(code, value);
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            model.call(adapter.getItem(position));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteButton:
                    model.keyDown(Keyboard.CODE_DELETE, "");
                    break;
                case R.id.showKeyboardButton:
                    keyboard.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.keyboard_showin_frombottom_up));
                    keyboard.setVisibility(View.VISIBLE);


            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.keyboard_hide_tobottom);
            if (keyboard.getVisibility() == View.VISIBLE && !animation.hasStarted()) {
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        keyboard.setVisibility(View.INVISIBLE);
                    }
                });
                keyboard.startAnimation(animation);

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.deleteButton:
                    model.clearNumber();
                    return true;
            }
            return false;
        }
    }

    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.abc_fragment_call, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        event = new CallEvent();
        model = new CallModel(getActivity());
        keyboard.setListener(event);
        adapter = new CallAdapter(getActivity(), model.getEmptyItems());
        searchListView.setAdapter(adapter);
        searchListView.setOnItemClickListener(event);
        searchListView.setOnScrollListener(event);
        showKeyboardButton.setOnClickListener(event);
        deleteButton.setOnClickListener(event);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            layoutChange = (LayoutChange) activity;
        } catch (Exception e) {
            throw new ClassCastException("must implement LayoutChange interface ok~");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        layoutChange = null;
    }

    @Override
    public int getPageTitle() {
        return R.string.call_fragment_title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

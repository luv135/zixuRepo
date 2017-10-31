package com.zigsun.mobile.ui.child;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.ContactsAdapter;
import com.zigsun.mobile.interfaces.LayoutChange;
import com.zigsun.mobile.interfaces.SoftInputVisible;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.observers.ContactObserver;
import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;
import com.zigsun.mobile.ui.base.AnimationListener;
import com.zigsun.mobile.ui.base.MulCallHeadLayout;
import com.zigsun.mobile.ui.base.TextWatcher;
import com.zigsun.mobile.ui.meeting.DialingActivity;
import com.zigsun.mobile.ui.personal.information.ContactsPersonalInformationActivity;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.no_screen.sort_contacts.SideBar;
import com.zigsun.ui.contact.SortModel;
import com.zigsun.util.CONSTANTS;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends AbsBaseViewPagerFragment implements SoftInputVisible, MulCallHeadLayout.ChangeListener, ContactObserver.ContactDataObserver {


    private static final String TAG = ContactsFragment.class.getSimpleName();
    @InjectView(R.id.searchEditText)
    EditText searchEditText;
    @InjectView(R.id.contactsListView)
    ListView contactsListView;
    @InjectView(R.id.callButton)
    ImageButton callButton;
    @InjectView(R.id.callButtonParentLayout)
    LinearLayout callButtonParentLayout;
    @InjectView(R.id.callVideoButton)
    ImageButton callVideoButton;
    @InjectView(R.id.dialog)
    TextView dialog;
    @InjectView(R.id.sidrbar)
    SideBar sidrbar;
    @InjectView(R.id.rootInterLayout)
    RelativeLayout rootInterLayout;
//    @InjectView(R.id.rootInterLayout)
//    InterRelativeLayout rootInterLayout;

    private ContactsModel module;
    private ContactsFragmentEvent event;
    private ContactsAdapter adapter;
    private MulCallHeadLayout headView;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            module.getContactsList(s.toString());
            adapter.notifyDataSetChanged();
        }
    };
    private boolean currentCanCall;
    private final List<SortModel> contactsList;

    public ContactsFragment() {
        // Required empty public constructor
        module = new ContactsModel();
        contactsList = module.getContactsList(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            layoutChange = (LayoutChange) activity;
        } catch (Exception e) {
            throw new ClassCastException("must implement LayoutChange interface ok~");
        }
        ContactObserver.getInstance().registerObserver(this);
        event = new ContactsFragmentEvent();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.abc_fragment_contacts, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ContactsAdapter(contactsListView, event, contactsList, getActivity());
        contactsListView.setAdapter(adapter);
        callButton.setOnClickListener(event);
        callVideoButton.setOnClickListener(event);
        searchEditText.addTextChangedListener(textWatcher);
        sidrbar.setTextView(dialog);
        // 设置右侧触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
//                    contactsListView.setSelection(position);
                    contactsListView.setSelectionFromTop(position, 0);
                }

            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        adapter.resetChecked();

    }

    private LayoutChange layoutChange;
    private SparseArray<View> slected = new SparseArray<>();


    private void onCheckedChanged(int userId, boolean checked, boolean canCall) {
        if (canCall) {
            if (!currentCanCall)
                canCall(userId);
            if (checked) {
                final TextView textView = new TextView(getActivity());
//                final SortModel item = (SortModel) adapter.getItem(index);

                final ContactItem sortModel = (ContactItem) Utils.find(adapter.getContacts()/*module.getContactsList(null)*/, userId);
                textView.setTextColor(Color.WHITE);
                textView.setText(sortModel.getNickName() + " ; ");
                textView.setTag(userId);
                slected.put(userId, textView);
                headView.addItemView(textView);
            } else {
                final View view1 = slected.get(userId);
                if (view1 != null)
                    headView.removeItem(view1);
            }
        } else canNotCall(userId);
        currentCanCall = canCall;

        if (!TextUtils.isEmpty(searchEditText.getText()))
            searchEditText.setText("");

    }

    public void canCall(int index) {
//        rootInterLayout.setInterception(true);
        if (headView == null) {
            headView = new MulCallHeadLayout(getActivity(), this);
            layoutChange.addView(headView);
        }
//        callButton.postDelayed(callButtonVisib,1000);
//        callButton.setVisibility(View.VISIBLE);

//        callButtonParentLayout.setVisibility(View.VISIBLE);
        showCallLayout();
    }

    private void hideCallLayout() {
        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.keyboard_hide_tobottom);
        if (callButtonParentLayout.getVisibility() == View.VISIBLE && !animation.hasStarted()) {
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    callButtonParentLayout.setVisibility(View.INVISIBLE);
                }
            });
            callButtonParentLayout.startAnimation(animation);

        }
    }

    private void showCallLayout() {
        if (headView == null) return;
        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.keyboard_showin_frombottom_up);
        if (callButtonParentLayout.getVisibility() != View.VISIBLE && !animation.hasStarted()) {
            callButtonParentLayout.startAnimation(animation);
            callButtonParentLayout.setVisibility(View.VISIBLE);
        }
    }

    private Runnable callButtonVisib = new Runnable() {
        @Override
        public void run() {
            callButton.setVisibility(View.VISIBLE);
        }
    };

    public void canNotCall(int index) {
//        callButton.removeCallbacks(callButtonVisib);
//        callButton.setVisibility(View.INVISIBLE);
        hideCallLayout();


//        rootInterLayout.setInterception(false);
        layoutChange.removeView(headView);
        headView = null;
    }

    @Override
    public int getPageTitle() {
        return R.string.contacts_fragment_title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        layoutChange = null;
        ContactObserver.getInstance().unRegisterObserver(this);

    }

    @Override
    public void showSoftInput(View view) {

    }

    @Override
    public void hideSoftInput(View view) {
        if (searchEditText != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            searchEditText.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void itemRemoeAll() {
        adapter.resetChecked();
    }


    @Override
    public void recentDateChange() {
        Log.d(TAG, "CONTACTFRAGMENT recentDateChange");
        module.getContactsList(null);
        adapter.notifyDataSetChanged();
    }


    private class ContactsFragmentEvent implements View.OnClickListener, ContactsAdapter.CallListener {

//        @Override
//        public void canCall(int index) {
//
//            ContactsFragment.this.canCall(index);
//        }
//
//        @Override
//        public void canNotCall(int index) {
//            ContactsFragment.this.canNotCall(index);
//        }

        @Override
        public void onCheckedChanged(int userId, boolean checked, boolean canCall) {
            ContactsFragment.this.onCheckedChanged(userId, checked, canCall);
        }

        @Override
        public void hideCallLayout() {
            ContactsFragment.this.hideCallLayout();
        }

        @Override
        public void showCallLayout() {
            ContactsFragment.this.showCallLayout();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ContactItem item = (ContactItem) adapter.getItem(position);
            final Intent intent = new Intent(getActivity(), ContactsPersonalInformationActivity.class);
            final UserInfo wrap = Utils.wrap(item);
            intent.putExtra(CONSTANTS.USERINFO, (Parcelable) wrap);
//            EMeetingApplication.getUserInfos().clear();
//            EMeetingApplication.addUserInfo(Utils.wrap(item));
            startActivity(intent);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callVideoButton:
                case R.id.callButton:
                    final Intent intent = new Intent(getActivity(), DialingActivity.class);
                    if (v.getId() == R.id.callVideoButton) intent.putExtra("OPEN_CAMERA", true);
                    EMeetingApplication.getUserInfos().clear();
                    final List<SortModel> canCallItem = adapter.getCanCallItem();
                    for (SortModel i : canCallItem) {
                        EMeetingApplication.addUserInfo(Utils.wrap(i));
                    }
                    startActivity(intent);
                    break;
            }
        }
    }


}

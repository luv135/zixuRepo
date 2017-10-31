package com.zigsun.mobile.ui.meeting;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.EMeetingApplication;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.DialAdapter;
import com.zigsun.mobile.interfaces.IDialMeeting;
import com.zigsun.mobile.interfaces.IVideo;
import com.zigsun.mobile.ui.base.CircleImageView;
import com.zigsun.mobile.ui.base.FragmentActivity;
import com.zigsun.mobile.ui.base.bubbles.Bubble;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * unuse
 */
@Deprecated
public class MeetingActivity extends FragmentActivity implements IDialMeeting.DialingStatus {

    @InjectView(R.id.multiGridM)
    GridView multiGridM;
    @InjectView(R.id.multiLayout)
    LinearLayout multiLayout;
    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;
    @InjectView(R.id.singleHeader)
    CircleImageView singleHeader;
    @InjectView(R.id.singleName)
    TextView singleName;
    @InjectView(R.id.singleLayout)
    LinearLayout singleLayout;
    @InjectView(R.id.callStatusText)
    TextView callStatusText;
    @InjectView(R.id.cameraOpenButton)
    ImageButton cameraOpenButton;
    @InjectView(R.id.cameraCloseButton)
    ImageButton cameraCloseButton;
    @InjectView(R.id.shareButton)
    ImageButton shareButton;
    @InjectView(R.id.shareNotButton)
    ImageButton shareNotButton;
    @InjectView(R.id.inviteButton)
    ImageButton inviteButton;
    @InjectView(R.id.hangFreeButton)
    ImageButton hangFreeButton;
    @InjectView(R.id.hangFreeNotButton)
    ImageButton hangFreeNotButton;
    @InjectView(R.id.hangUpButton)
    ImageButton hangUpButton;
    @InjectView(R.id.muteButton)
    ImageButton muteButton;
    @InjectView(R.id.muteNotButton)
    ImageButton muteNotButton;
    @InjectView(R.id.meetingLayout)
    LinearLayout meetingLayout;
//    @InjectView(R.id.bubbleComingLayout)
//    Bubble bubbleComingLayout;
    private DialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_meeting);
        ButterKnife.inject(this);
        adapter = new DialAdapter(this, EMeetingApplication.getUserInfos());
        multiGridM.setAdapter(adapter);

//        singleName.setText(EMeetingApplication.getUserInfos().get(0).getStrUserName());

        prepareSurface();
    }

    private void prepareSurface() {
        final RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) surfaceView
                .getLayoutParams();

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            public static final float MAX_TAP_DISTANCE = 5;
            public boolean isMoved;
            public float oldOffsetY;
            public float oldOffsetX;
            public float firstRawY;
            public float firstRawX;
            public float height = Util.getScreenResolution(getApplicationContext()).y / 2;
            public float width = Util.getScreenResolution(getApplicationContext()).x;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoved = false;
                        firstRawX = event.getRawX();
                        firstRawY = event.getRawY();
                        oldOffsetX = v.getTranslationX(); // 偏移量
                        oldOffsetY = v.getTranslationY(); // 偏移量
//                        Log.d(TAG, "ACTION_DOWN");
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (!isMoved) {
                            float nowX = v.getTranslationX(); // 偏移量
                            float nowY = v.getTranslationY(); // 偏移量

                            if (Math.abs(oldOffsetX - nowX) > MAX_TAP_DISTANCE || Math.abs(oldOffsetY - nowY) > MAX_TAP_DISTANCE) {
                                isMoved = true;
                                MotionEvent cancelEvent = MotionEvent.obtain(event);
                                cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                                v.onTouchEvent(cancelEvent);
                            }
                        }

                        float x = oldOffsetX + (event.getRawX() - firstRawX);// - mFloatView.getMeasuredWidth() / 2;

                        float y = oldOffsetY + (event.getRawY() - firstRawY);// - mFloatView.getMeasuredHeight() / 2 - 25;
                        if (x > 0 && x < width - v.getWidth() * 2)
                            v.setTranslationX(x);
                        if (y > 0 && y < height - v.getHeight())
                            v.setTranslationY(y);
//                        Log.d(TAG, "ACTION_MOVE x= " + x + " y= " + y);
                        return true;
                    case MotionEvent.ACTION_UP:
//                        v.animate().translationY(0).translationX(0).start();
                        return isMoved;

                }
                return false;
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            boolean needFullScreen = false;

            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClick:++");
                v.setTranslationX(0);
                v.setTranslationY(0);

                if (needFullScreen) {
//                    layout5.setVisibility(View.GONE);
                    surfaceView
                            .setLayoutParams(new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT));
                    needFullScreen = !needFullScreen;
                } else {
//                    layout5.setVisibility(View.VISIBLE);
                    surfaceView.setLayoutParams(linearParams);
                    needFullScreen = !needFullScreen;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeLayout();
    }

    private void changeLayout() {
        if (EMeetingApplication.getUserInfos().size() > 1) {
            singleLayout.setVisibility(View.INVISIBLE);
            multiLayout.setVisibility(View.VISIBLE);
        } else {
            singleLayout.setVisibility(View.VISIBLE);
            multiLayout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void DialingWithNumber(String number) {
        callStatusText.setText(R.string.connecting);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void acceptedWithNumber(String number) {
        callStatusText.setText(number + " " + getString(R.string.connected));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void rejectedWithNumber(String number) {
        callStatusText.setText(number + " " + getString(R.string.rejected));
        adapter.notifyDataSetChanged();
    }

    /**
     * @param number 谁已断开, null房主结束会议.
     */
    @Override
    public void hangUpWithNumber(String number) {
        callStatusText.setText(number + " " + getString(R.string.disconnected));
        if (number == null) finish();
    }

    @Override
    public void onLineNotify(long ulUserID) {
//        Log.d(TAG, "onLineNotify()" + ulUserID);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void offLineNotify(long ulUserID) {
//        Log.d(TAG, "offLineNotify()" + ulUserID);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void leaveRoomNotify(long ulUserID) {
        if (adapter.checkPeopInMeeting() <= 0) finish();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void joinRoomNotify(long ulUserID) {
//        Log.d(TAG, "joinRoomNotify()" + ulUserID);
        adapter.notifyDataSetChanged();
//        model.endDial();
    }

//    @Override
//    public void onBackPressed() {
//    }

    @Override
    public void videoAction(IVideo.Action action) {
//        Toast.makeText(this, "video action: " + action, Toast.LENGTH_SHORT).show();
        switch (action) {
            case CLOSE_SUCCESS:
                cameraCloseButton.setVisibility(View.INVISIBLE);
                cameraOpenButton.setVisibility(View.VISIBLE);
                break;
            case OPEN_SUCCESS:
                cameraOpenButton.setVisibility(View.INVISIBLE);
                cameraCloseButton.setVisibility(View.VISIBLE);
                break;
        }
    }

}

package com.zigsun.mobile.ui.meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.core.CameraHelper;
import com.zigsun.core.ConferenceMgr;
import com.zigsun.db.RecentDBHelper;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.DialAdapter;
import com.zigsun.mobile.interfaces.IDialMeeting;
import com.zigsun.mobile.interfaces.IVideo;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.model.MeetingModel;
import com.zigsun.mobile.model.MeetingStatusRecorder;
import com.zigsun.mobile.module.CallStatus;
import com.zigsun.mobile.ui.base.bubbles.Bubble;
import com.zigsun.mobile.utils.RecentUtil;
import com.zigsun.mobile.utils.ScreenSensorHelper;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.util.CONSTANTS;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 呼入
 */
public class IncomingActivity extends FragmentActivity implements IDialMeeting.DialingStatus {

    private static final String TAG = IncomingActivity.class.getSimpleName();


    @InjectView(R.id.mSur)
    SurfaceView mSurfaceView;


    @InjectView(R.id.cameraOpenButton)
    ImageButton cameraOpenButton;
    @InjectView(R.id.cameraCloseButton)
    ImageButton cameraCloseButton;
    @InjectView(R.id.inviteButton)
    ImageButton inviteButton;
    @InjectView(R.id.hangUpButton)
    ImageButton hangUpButton;
    @InjectView(R.id.mulGridView)
    GridView mulGridView;
    @InjectView(R.id.avatarImageView)
    ImageView avatarImageView;
    @InjectView(R.id.dialingStatusTextView)
    StatusText dialingStatusTextView;
    @InjectView(R.id.shareButton)
    ImageButton shareButton;
    @InjectView(R.id.hangFreeButton)
    ImageButton hangFreeButton;
    @InjectView(R.id.muteButton)
    ImageButton muteButton;
    @InjectView(R.id.conversationLayout)
    View conversationLayout;
    @InjectView(R.id.multLayout)
    LinearLayout multLayout;
    @InjectView(R.id.hangFreeNotButton)
    ImageButton hangFreeNotButton;
    @InjectView(R.id.muteNotButton)
    ImageButton muteNotButton;

    @InjectView(R.id.shareNotButton)
    ImageButton shareNotButton;


    @InjectView(R.id.incomingLayout)
    Bubble incomingLayout;


    @InjectView(R.id.meetingLayout)
    LinearLayout meetingLayout;
    @InjectView(R.id.nameText)
    TextView nameText;
    @InjectView(R.id.singleLayout)
    LinearLayout singleLayout;
    @InjectView(R.id.returnButton)
    Button returnButton;
    @InjectView(R.id.hangUpBtn)
    Button hangUpBtn;
    @InjectView(R.id.acceptBtn)
    Button acceptBtn;
    @InjectView(R.id.inLayout)
    LinearLayout inLayout;

    private MeetingModel dialMeeting;
    private CameraHelper cameraHelper;
    private MeetingStatusRecorder recorder;
    private IncomingActivityEvent event;
    private DialAdapter adapter;
    private boolean needFullScreen = false;
    private ViewGroup.LayoutParams linearParams;
    private MyHandler handler;
    private long ulMeetingID;
    private long ulUserID;
    private String strNickName;
    private long meetingType;
    private RingSing ringSing;

    @Override
    public void onLineNotify(long ulUserID) {
        if (EMeetingApplication.getUserInfos().size() > 1) {
            avatarImageView.setVisibility(View.INVISIBLE);
            mulGridView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "mulGridView.setVisibility(View.VISIBLE)");
        } else {
            avatarImageView.setVisibility(View.VISIBLE);
            mulGridView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void offLineNotify(long ulUserID) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void leaveRoomNotify(long ulUserID) {
        if (adapter.checkPeopInMeeting() <= 0) finish();
        if (EMeetingApplication.getUserInfos().size() > 1) {
            avatarImageView.setVisibility(View.INVISIBLE);
            mulGridView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "mulGridView.setVisibility(View.VISIBLE)");
        } else {
            avatarImageView.setVisibility(View.VISIBLE);
            mulGridView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void joinRoomNotify(long ulUserID) {
        adapter.notifyDataSetChanged();
    }

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

    private static class MyHandler extends Handler {
        private WeakReference<IncomingActivity> reference;

        public MyHandler(IncomingActivity activity) {
            reference = new WeakReference<IncomingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            IncomingActivity activity = reference.get();

            switch (msg.what) {
                case CONSTANTS.START_CAPTURE_VIDEO:
                    Log.d(TAG, "开始广播视频......");
                    activity.cameraHelper.setCapture(true);
                    break;
                case CONSTANTS.MESSAGE_WHATE_INIT_SURFACE:
                    activity.initSurface();
                    break;
                case CONSTANTS.MESSAGE_WHATE_stopVideoRecord:
                    activity.stopVideoRecord();
                    break;
                case CONSTANTS.MESSAGE_WHATE_SURFACE_GONE:
                    activity.mSurfaceView.setVisibility(View.GONE);
                    break;
                case CONSTANTS.ACCEPT:
                    activity.accpt();
                    break;
                case CONSTANTS.REJCT:
                    activity.reject();
                    break;

            }
        }
    }

    private void reject() {
        reject(ulMeetingID, ulUserID, strNickName);
    }

    private void accpt() {
        accpt(ulMeetingID, ulUserID, meetingType);
    }

    private void stopVideoRecord() {
        cameraHelper.closeCamera(EMeetingApplication
                .getUserInfo().getUlUserID());
        mSurfaceView.setVisibility(View.GONE);
    }

    private void initSurface() {
        mSurfaceView.setVisibility(View.VISIBLE);
        // 移植自MeetingActivity
        cameraHelper.SelectVideoCapture(cameraHelper.CAMERA_FACING_FRONT);
        mSurfaceView.getHolder().addCallback(cameraHelper);
    }

    private class IncomingActivityEvent implements View.OnClickListener, Bubble.ActionListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hangUpButton:
                case R.id.returnButton:
//                    dialMeeting.hangUp();
                    finish();
                    break;
                case R.id.cameraCloseButton:
                    dialMeeting.stopVideoRecord();
                    break;
                case R.id.cameraOpenButton:
                    dialMeeting.startVideoRecord();
                    break;
                case R.id.muteButton:
                    model.mute();
                    muteButton.setVisibility(View.INVISIBLE);
                    muteNotButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.muteNotButton:
                    model.muteNot();
                    muteButton.setVisibility(View.VISIBLE);
                    muteNotButton.setVisibility(View.INVISIBLE);
                    break;
                case R.id.hangFreeButton:
                    hangFreeButton.setVisibility(View.INVISIBLE);
                    hangFreeNotButton.setVisibility(View.VISIBLE);
                    model.hangFree();
                    break;
                case R.id.hangFreeNotButton:
                    hangFreeNotButton.setVisibility(View.INVISIBLE);
                    hangFreeButton.setVisibility(View.VISIBLE);
                    model.hangFreeNt();
                    break;
                case R.id.acceptBtn:
                    acceptCall();
                    inLayout.setVisibility(View.GONE);
                    break;
                case R.id.hangUpBtn:
                    rejectCall();
                    inLayout.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void acceptCall() {
            accpt();
        }

        @Override
        public void rejectCall() {
            reject();
        }

        @Override
        public void messageCall() {

        }
    }

    private DiaLingModel model;

    private class DiaLingModel {
        public DiaLingModel() {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

        private AudioManager am;
        private int current;

        private void muteNot() {
            if (EMeetingApplication.getBrocastStatus() == 2) {// 静音前是音频
                ConferenceMgr.SetBrocastMember(EMeetingApplication
                        .getUserInfo().getUlUserID(), 2);
            } else if (EMeetingApplication.getBrocastStatus() == 0) {// 静音前是音视频
                ConferenceMgr.SetBrocastMember(EMeetingApplication
                        .getUserInfo().getUlUserID(), 0);
            }
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, current,
                    0);
        }

        private void mute() {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            current = am
                    .getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, 0);
            if (EMeetingApplication.getBrocastStatus() == 2) {// 之前广播的是音频
                ConferenceMgr.SetBrocastMember(EMeetingApplication
                        .getUserInfo().getUlUserID(), 3);// 关闭声音，什么都不广播
            } else if (EMeetingApplication.getBrocastStatus() == 0) {// 之前广播的是音视频
                ConferenceMgr.SetBrocastMember(EMeetingApplication
                        .getUserInfo().getUlUserID(), 1);// 关闭声音，只广播视频
            }
//        silence.setImageResource(R.drawable.u67);
        }

        public void hangFreeNt() {
//            am.setSpeakerphoneOn(false);//关闭扬声器
//            am.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
//            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
//            //把声音设定成Earpiece（听筒）出来，设定为正在通话中
            Log.d(TAG, "FREE HANG NT");
            am.setMode(AudioManager.MODE_IN_CALL);
            am.setSpeakerphoneOn(false);
        }

        public void hangFree() {
//            am.setSpeakerphoneOn(true);
            Log.d(TAG, "FREE HANG YEAH");
            am.setSpeakerphoneOn(true);
            am.setMode(AudioManager.MODE_NORMAL);
        }

    }

    @Override
    public void onBackPressed() {
    }

    private ScreenSensorHelper screenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.abc_activity_call_in);// 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        screenHelper = new ScreenSensorHelper(this);

        handler = new MyHandler(this);
        dialMeeting = new MeetingModel(this, handler);
        cameraHelper = new CameraHelper(this);
        recorder = new MeetingStatusRecorder(System.currentTimeMillis(), CallStatus.InNoAnswer);
        event = new IncomingActivityEvent();
        model = new DiaLingModel();
        conversationLayout.setVisibility(View.INVISIBLE);
        inviteButton.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        ulMeetingID = bundle.getLong(CONSTANTS.UL_MEETING_ID);
        ulUserID = bundle.getLong(CONSTANTS.EXTRA_UL_USER_ID);
        strNickName = TextUtils.isEmpty(bundle.getString(CONSTANTS.EXTRA_STR_NICK_NAME)) ? "nima" : bundle.getString(CONSTANTS.EXTRA_STR_NICK_NAME);
        meetingType = bundle.getLong(CONSTANTS.EXTRA_MEETING_TYPE);
        Util.setOnClickListener(event, hangUpBtn, acceptBtn, returnButton, hangUpButton, cameraOpenButton, cameraCloseButton, hangFreeButton, hangFreeNotButton, muteNotButton, muteButton);


//        new AnsModel(this,toAcceptButton, toRejectButton, startClickButton, toMessageButton);
        incomingLayout.activateAcceptListener().activateRejectListener().setActivateListener(event);

//        toAcceptButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                accpt(ulMeetingID, ulUserID, meetingType);
//            }
//        });
//        toRejectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reject(ulMeetingID, ulUserID, strNickName);
//            }
//        });


        final UserInfo wrap = Utils.wrap(new ContactsModel().queryByID(ulUserID), ulUserID);
        wrap.setUcStatus((byte) 2);
        EMeetingApplication.addUserInfo(wrap);
        nameText.setText(new ContactsModel().qureyName(wrap.getStrUserName()));
        adapter = new DialAdapter(this, EMeetingApplication.getUserInfos());
        mulGridView.setAdapter(adapter);
        linearParams = (RelativeLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        mSurfaceView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setTranslationX(0);
                v.setTranslationY(0);
                if (needFullScreen) {
//                    layout5.setVisibility(View.GONE);
                    mSurfaceView
                            .setLayoutParams(new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT));
                    needFullScreen = !needFullScreen;
                } else {
//                    layout5.setVisibility(View.VISIBLE);
                    mSurfaceView.setLayoutParams(linearParams);
                    needFullScreen = !needFullScreen;
                }
            }
        });
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
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
                        Log.d(TAG, "ACTION_DOWN");
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
                        Log.d(TAG, "ACTION_MOVE x= " + x + " y= " + y);
                        return true;
                    case MotionEvent.ACTION_UP:
//                        v.animate().translationY(0).translationX(0).start();
                        return isMoved;

                }
                return false;
            }
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction(CONSTANTS.EMEETING_ACTION_END);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(br, filter);
        ringSing = new RingSing();
        ringSing.ringRing();
    }


    private class RingSing {
        private MediaPlayer player;

        public void ringRing() {
            final AudioManager systemService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            systemService.setSpeakerphoneOn(true);
            systemService.setMode(AudioManager.MODE_NORMAL);
            final Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            player = new MediaPlayer();
            player.setLooping(true);
            try {
                player.setDataSource(IncomingActivity.this, defaultUri);
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void shutUp() {
            if (player.isPlaying())
                player.stop();
            model.hangFreeNt();
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "incoming broadcast " + action);
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                    ringSing.shutUp();
                    break;
                case CONSTANTS.EMEETING_ACTION_END:
                    if (!intent.getBooleanExtra("isMeetingExist", true)) {
                        finish();
                    }
                    break;
            }

        }
    };

    private void reject(long ulMeetingID, long ulUserID, String strNickName) {
        recorder.setStatus(CallStatus.InReject);
        dialMeeting.reject(ulMeetingID, ulUserID, strNickName);
        ringSing.shutUp();
        finish();
    }


    private void accpt(long ulMeetingID, long ulUserID, long meetingType) {
        recorder.setStatus(CallStatus.InAccept);
        dialMeeting.accept(ulMeetingID, ulUserID, meetingType);
        conversationLayout.setVisibility(View.VISIBLE);
        ringSing.shutUp();

//        incomingLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        screenHelper.registerListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        screenHelper.unregisterListener();

    }

    @Override
    protected void onDestroy() {
        ringSing.shutUp();
        unregisterReceiver(br);
        dialMeeting.hangUp();
        final RecentDBHelper recentDb = new RecentDBHelper();
        String[] people = new String[EMeetingApplication.getUserInfos().size()];
        long[] userIds = new long[EMeetingApplication.getUserInfos().size()];
      /*  final SparseArray<String> array = */
        Utils.getUsrInfoNamesAndUserId(EMeetingApplication.getUserInfos(), people, userIds);
        recentDb.create(RecentUtil.wrapItem(recorder.getStartTime(), System.currentTimeMillis(),
                recorder.getStatus(), people, userIds
        ));
        EMeetingApplication.getUserInfos().clear();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.abc_action_add_contact) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DialingWithNumber(String number) {
        adapter.notifyDataSetChanged();

    }

    @Override
    public void acceptedWithNumber(String number) {
        adapter.notifyDataSetChanged();

    }

    @Override
    public void rejectedWithNumber(String number) {
        adapter.notifyDataSetChanged();

    }

    @Override
    public void hangUpWithNumber(String number) {
        if (number == null) finish();
    }
}

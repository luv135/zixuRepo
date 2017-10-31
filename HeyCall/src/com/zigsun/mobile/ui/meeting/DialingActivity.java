package com.zigsun.mobile.ui.meeting;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zigsun.mobile.model.MeetingModel;
import com.zigsun.mobile.model.MeetingStatusRecorder;
import com.zigsun.mobile.module.CallStatus;
import com.zigsun.mobile.module.DialingUserInfo;
import com.zigsun.mobile.module.UserInfoStatus;
import com.zigsun.mobile.receivers.CallInReceiver;
import com.zigsun.mobile.utils.RecentUtil;
import com.zigsun.mobile.utils.ScreenSensorHelper;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.util.CONSTANTS;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 呼出
 */
public class DialingActivity extends FragmentActivity implements IDialMeeting.DialingStatus {

    public static final String CALL_NUMBER = DialingActivity.class.getSimpleName() + ".CALL_NUMBER";
    public static final String TAG = DialingActivity.class.getSimpleName();
    @InjectView(R.id.dialingStatusTextView)
    StatusText dialingStatusTextView;
    @InjectView(R.id.cameraOpenButton)
    ImageButton cameraOpenButton;
    @InjectView(R.id.shareButton)
    ImageButton shareButton;
    @InjectView(R.id.inviteButton)
    ImageButton inviteButton;
    @InjectView(R.id.hangFreeButton)
    ImageButton hangFreeButton;
    @InjectView(R.id.hangUpButton)
    ImageButton hangUpButton;
    @InjectView(R.id.muteButton)
    ImageButton muteButton;
    @InjectView(R.id.mSur)
    SurfaceView mSurfaceView;
    @InjectView(R.id.mulGridView)
    GridView mulGridView;
    @InjectView(R.id.multLayout)
    LinearLayout multLayout;
    @InjectView(R.id.avatarImageView)
    ImageView avatarImageView;
    @InjectView(R.id.cameraCloseButton)
    ImageButton cameraCloseButton;
    @InjectView(R.id.hangFreeNotButton)
    ImageButton hangFreeNotButton;
    @InjectView(R.id.muteNotButton)
    ImageButton muteNotButton;
    @InjectView(R.id.userNameText)
    TextView userNameText;
    @InjectView(R.id.shareNotButton)
    ImageButton shareNotButton;
    @InjectView(R.id.returnButton)
    Button returnButton;
    @InjectView(R.id.tickTokText)
    TextView tickTokText;

    private IDialMeeting dialMeeting;
    private DialingSingleActivityEvent event;
    private CameraHelper cameraHelper;
    private DialAdapter adapter;
    private MeetingStatusRecorder recorder;
    private DiaLingModel model;
    private RelativeLayout.LayoutParams linearParams;
    private boolean needFullScreen = false;
    private MyHandler handler;

    private class DiaLingModel {
        public static final int HANGUP_DELAY_MILLIS = 60000;
        private final SoundPool sp;
        private int music;
        private Context context;
        private boolean needdudu;
        private HashMap<Integer, Boolean> rejectMap;

        public DiaLingModel(Context context) {
            this.context = context;
            rejectMap = new HashMap<>();
            ;
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (needdudu)
                        Log.d(TAG, "dial MUSIC: " + sp.play(music, 1, 1, 0, -1, 1));
                }
            });
            music = sp.load(context, R.raw.abc_dudu, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        }


        private AudioManager am;

        public void rejectedWithUid(long ulUserID) {
            rejectMap.put((int) ulUserID, true);
            Log.d(TAG, "rejectedWithUid");
        }

        public boolean canCall(long ulUserId) {
            final boolean b = rejectMap.get((int) ulUserId) == null;
            Log.d(TAG, "canCall: " + b);
            return b;
        }

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

        public void dial() {
            needdudu = true;
            if (0 == sp.play(music, 1, 1, 0, -1, 1)) {
            }
            handler.sendEmptyMessageDelayed(MyHandler.HANGUPB, HANGUP_DELAY_MILLIS);

        }

        public void endDial() {
            sp.stop(music);
            hangFreeNt();
            needdudu = false;
            Log.d(TAG, "END DUDUDUUDU");
            handler.removeMessages(MyHandler.HANGUPB);
        }
    }


    @Override
    public void onLineNotify(long ulUserID) {
        final DialingUserInfo user = Utils.findUserById(EMeetingApplication.getUserInfos(), ulUserID);
        assert user != null;
        String number = user.getStrNickName();
        dialingStatusTextView.setStatus(number + getString(R.string.on_line));
        Log.d(TAG, "onLineNotify()" + ulUserID);
//        final DialingUserInfo userById = Utils.findUserById(EMeetingApplication.getUserInfos(), ulUserID);
//        assert userById != null;
        if (model.canCall(ulUserID))
            dialMeeting.inviteToMeeting(ulUserID);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void offLineNotify(long ulUserID) {
        Log.d(TAG, "offLineNotify()" + ulUserID);
        final DialingUserInfo user = Utils.findUserById(EMeetingApplication.getUserInfos(), ulUserID);
        assert user != null;
        String number = user.getStrNickName();
        dialingStatusTextView.setStatus(number + getString(R.string.off_line));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void leaveRoomNotify(long ulUserID) {
        if (adapter.checkPeopInMeeting() <= 0) finish();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void joinRoomNotify(long ulUserID) {
        Log.d(TAG, "joinRoomNotify()" + ulUserID);
        adapter.notifyDataSetChanged();
        model.endDial();
    }

    @Override
    public void onBackPressed() {
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
        private static final int HANGUPB = 04;
        private WeakReference<DialingActivity> reference;

        public MyHandler(DialingActivity activity) {
            reference = new WeakReference<DialingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DialingActivity activity = reference.get();

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
                case HANGUPB:
                    activity.timeOutHangup();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void timeOutHangup() {
        Toast.makeText(this, R.string.abc_no_ans_end_up, Toast.LENGTH_SHORT).show();
        hangUpButton.performClick();
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

    private static final int INVITED = 0100;

    private class DialingSingleActivityEvent implements View.OnClickListener, AdapterView.OnItemClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hangUpButton:
                case R.id.returnButton:
//                    dialMeeting.hangUp();
                    finish();
                    break;
                case R.id.inviteButton:
                    startActivityForResult(new Intent(DialingActivity.this, InvitedJoinMeetingActivity.class), INVITED);
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
            }

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == INVITED && resultCode == RESULT_OK) {
            final List<UserInfo> userInfos = EMeetingApplication.getInViteduserInfos();
            for (UserInfo i : userInfos) {
                EMeetingApplication.addUserInfo(i);
                Log.d(TAG, "invite id: " + i.getUlUserID());
                dialMeeting.inviteToMeeting(i.getUlUserID());
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setClickListener() {
        Util.setOnClickListener(event, returnButton, hangUpButton, hangFreeButton, hangFreeNotButton, muteButton, muteNotButton, inviteButton, cameraOpenButton, cameraCloseButton);
    }


    public class TickTok {
        Timer timer;

        public TickTok(int seconds) {
            timer = new Timer();
            // 在1000秒后,每seconds*1000执行一次
            timer.schedule(new RemindTask(), 1000, seconds * 1000);
        }

        class RemindTask extends TimerTask {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                    tickTokText.setText();
                    }
                });

            }
        }
    }

    private ScreenSensorHelper screenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_dialing_single);
        // 屏幕常亮
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Intent intent = new Intent(CallInReceiver.COM_ZIGSUN_DIALING);
        intent.putExtra(CallInReceiver.DIALING, true);
        sendBroadcast(intent);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_title_background));
        }
        ButterKnife.inject(this);
        screenHelper = new ScreenSensorHelper(this);
        event = new DialingSingleActivityEvent();
        handler = new MyHandler(this);
        dialMeeting = new MeetingModel(this, handler);
        model = new DiaLingModel(this);
        cameraHelper = new CameraHelper(this);
        recorder = new MeetingStatusRecorder(System.currentTimeMillis(), CallStatus.DialOut);
        adapter = new DialAdapter(this, EMeetingApplication.getUserInfos());
        setClickListener();

        mulGridView.setAdapter(adapter);
        mulGridView.setOnItemClickListener(event);
        registerForContextMenu(mulGridView);
//        final String number = getIntent().getStringExtra(CALL_NUMBER);
        dialMeeting.dial(null);
        model.dial();
        userNameText.setText(EMeetingApplication.getUserInfos().get(0).getStrNickName());
        linearParams = (RelativeLayout.LayoutParams) mSurfaceView
                .getLayoutParams();


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
        mSurfaceView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:++");
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
//        final boolean open_camera = getIntent().getBooleanExtra("OPEN_CAMERA", false);
//        if (open_camera) {
//            cameraOpenButton.performClick();
//        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        menu.setHeaderTitle("position: " + info.position);
        getMenuInflater().inflate(R.menu.dial_grid_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final UserInfo item1 = adapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
            case R.id.action_inv:
                dialMeeting.inviteToMeeting(item1.getUlUserID());
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_remove:
                dialMeeting.kickOut(item1.getUlUserID());
                EMeetingApplication.getUserInfos().remove(item1);
                adapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenHelper.registerListener();
        Log.d(TAG, "onResume");
        if (EMeetingApplication.getUserInfos().size() > 1) {
            avatarImageView.setVisibility(View.INVISIBLE);
            userNameText.setVisibility(View.INVISIBLE);
            mulGridView.setVisibility(View.VISIBLE);
            Log.d(TAG, "mulGridView.setVisibility(View.VISIBLE)");
        } else {
            avatarImageView.setVisibility(View.VISIBLE);
            userNameText.setVisibility(View.VISIBLE);
            mulGridView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        model.endDial();
        screenHelper.unregisterListener();
    }

    @Override
    protected void onDestroy() {
        dialMeeting.hangUp();
        model.endDial();
        final Intent intent = new Intent(CallInReceiver.COM_ZIGSUN_DIALING);
        intent.putExtra(CallInReceiver.DIALING, false);
        sendBroadcast(intent);
        Log.d(TAG, "onDestroy");
      /*  final SparseArray<String> array = */
        if (EMeetingApplication.getUserInfos().size() > 0) {
            final RecentDBHelper recentDb = new RecentDBHelper();
            String[] people = new String[EMeetingApplication.getUserInfos().size()];
            long[] userIds = new long[EMeetingApplication.getUserInfos().size()];
            Utils.getUsrInfoNamesAndUserId(EMeetingApplication.getUserInfos(), people, userIds);
            recentDb.create(RecentUtil.wrapItem(recorder.getStartTime(), System.currentTimeMillis(),
                    recorder.getStatus(), people, userIds
            ));
            EMeetingApplication.getUserInfos().clear();
        }
        super.onDestroy();
    }


    @Override
    public void DialingWithNumber(String number) {
//        dialingStatusTextView.setStatus(number==null?"":number + getString(R.string.connecting));
        Log.d(TAG, "DialingWithNumber: " + number);
        if (number != null) {
            final DialingUserInfo user = Utils.findUser(EMeetingApplication.getUserInfos(), (number));
            assert user != null;
            number = user.getStrNickName();
            dialingStatusTextView.setStatus(number + getString(R.string.connecting));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void acceptedWithNumber(String number) {

//        final DialingUserInfo user = Utils.findUserById(EMeetingApplication.getUserInfos(), Long.parseLong(number));
//        assert user != null;
//        number = user.getStrNickName();
        number = "";
        dialingStatusTextView.setStatus(number + getString(R.string.connected));
        recorder.setStatus(CallStatus.OutAccept);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void rejectedWithNumber(String number) {
//        dialingStatusTextView.setStatus(number + getString(R.string.rejected));
//        dialingStatusTextView.setStatus(getString(R.string.rejected));
//        dialingStatusTextView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialingStatusTextView.setVisibility(View.INVISIBLE);
//            }
//        },2000);
        model.endDial();
        Log.d(TAG, "rejectedWithNumber: " + number);
        recorder.setStatus(CallStatus.OutReject);
        adapter.notifyDataSetChanged();
        final DialingUserInfo user = Utils.findUserById(EMeetingApplication.getUserInfos(), Long.parseLong(number));
        assert user != null;
        String stat = /*user.getStrNickName() + */getString(R.string.rejected);
        dialingStatusTextView.setStatus(stat);
        model.rejectedWithUid(user.getUlUserID());
    }

    /**
     * @param number 谁已断开, null房主结束会议.
     */
    @Override
    public void hangUpWithNumber(String number) {
        if (number == null) finish();
        number = "";

        dialingStatusTextView.setStatus(number + getString(R.string.disconnected));
    }


}

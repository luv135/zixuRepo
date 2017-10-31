//package com.zigsun.mobile.ui.meeting;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.zigsun.EMeetingApplication;
//import com.zigsun.bean.UserInfo;
//import com.zigsun.core.CameraHelper;
//import com.zigsun.luo.projection.utils.Util;
//import com.zigsun.mobile.R;
//import com.zigsun.mobile.adapter.DialAdapter;
//import com.zigsun.mobile.interfaces.IDialMeeting;
//import com.zigsun.mobile.module.MeetingModule;
//import com.zigsun.util.CONSTANTS;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//
///**
// * Created by Luo on 2015/6/25.
// */
//
//public class BaseDialingActivity extends FragmentActivity implements IDialMeeting.DialingStatus {
//
//    public static final String CALL_NUMBER = DialingActivity.class.getSimpleName() + ".CALL_NUMBER";
//    public static final String TAG = DialingActivity.class.getSimpleName();
//
//    private IDialMeeting dialMeeting;
//    private DialingSingleActivityEvent event;
//    private CameraHelper cameraHelper;
//    private DialAdapter adapter;
//
//    @Override
//    public void onLineNotify(long ulUserID) {
//        Log.d(TAG, "onLineNotify()" + ulUserID);
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void offLineNotify(long ulUserID) {
//        Log.d(TAG, "offLineNotify()" + ulUserID);
//        adapter.notifyDataSetChanged();
//
//    }
//
//    @Override
//    public void leaveRoomNotify(long ulUserID) {
//        adapter.notifyDataSetChanged();
//
//    }
//
//    @Override
//    public void joinRoomNotify(long ulUserID) {
//        Log.d(TAG, "joinRoomNotify()" + ulUserID);
//        adapter.notifyDataSetChanged();
//
//
//    }
//
//    private static class MyHandler extends Handler {
//        private WeakReference<DialingActivity> reference;
//
//        public MyHandler(DialingActivity activity) {
//            reference = new WeakReference<DialingActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            DialingActivity activity = reference.get();
//
//            switch (msg.what) {
//                case CONSTANTS.START_CAPTURE_VIDEO:
//                    Log.d(TAG, "开始广播视频......");
//                    activity.cameraHelper.setCapture(true);
//                    break;
//                case CONSTANTS.MESSAGE_WHATE_INIT_SURFACE:
//                    activity.initSurface();
//                    break;
//            }
//        }
//    }
//
//    private void initSurface() {
//        mSurfaceView.setVisibility(View.VISIBLE);
//        // 移植自MeetingActivity
//        cameraHelper.SelectVideoCapture(cameraHelper.CAMERA_FACING_FRONT);
//        mSurfaceView.getHolder().addCallback(cameraHelper);
//    }
//
//    private static final int INVITED = 0100;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//
//
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        dialMeeting.hangUp();
//        super.onDestroy();
//
//    }
//
//    @Override
//    public void DialingWithNumber(String number) {
//        dialingStatusTextView.setText(R.string.connecting);
//        adapter.notifyDataSetChanged();
//
//    }
//
//    @Override
//    public void acceptedWithNumber(String number) {
//        dialingStatusTextView.setText(R.string.connected);
//        adapter.notifyDataSetChanged();
//
//    }
//
//    @Override
//    public void rejectedWithNumber(String number) {
//        dialingStatusTextView.setText(R.string.rejected);
//        adapter.notifyDataSetChanged();
//
//    }
//
//
//    /**
//     * @param number 谁已断开, null房主结束会议.
//     */
//    @Override
//    public void hangUpWithNumber(String number) {
//        dialingStatusTextView.setText(R.string.disconnected);
//        if (number == null) finish();
//    }
//
//
//}
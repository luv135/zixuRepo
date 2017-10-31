package com.zigsun.mobile.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.ui.meeting.IncomingActivity;
import com.zigsun.mobile.ui.meeting.MeetingActivity;
import com.zigsun.util.CONSTANTS;

public class CallInReceiver extends BroadcastReceiver {
    private static final String TAG = CallInReceiver.class.getSimpleName();
    public static final String DIALING = "CallInReceiver.DIALING";
    public static final String COM_ZIGSUN_DIALING = "com.zigsun.dialing";
    public static final String COM_ZIGUSN_LOGIN = "com.zigusn.login";


    public CallInReceiver() {
        Log.d(TAG, "CallInReceiver()");
    }

    /**
     * 呼叫状态
     */
    public static boolean busy;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case COM_ZIGSUN_DIALING:
                busy = intent.getBooleanExtra(DIALING, false);
                Log.d(TAG, "COM_ZIGSUN_DIALING busy="+busy);
                break;
            case COM_ZIGUSN_LOGIN:
                int flag = intent.getExtras().getInt(CONSTANTS.EXTRA_FLAG);
                Log.d(TAG, "MEETING_INVITE busy="+busy);
                if (flag == CONSTANTS.MEETING_INVITE  ) {
                    if(busy) {
                        Bundle bundle = intent.getExtras();
                        long ulMeetingID = bundle.getLong(CONSTANTS.UL_MEETING_ID);
                        long ulUserID = bundle.getLong(CONSTANTS.EXTRA_UL_USER_ID);
                        String strNickName = TextUtils.isEmpty(bundle.getString(CONSTANTS.EXTRA_STR_NICK_NAME)) ? "nima" : bundle.getString(CONSTANTS.EXTRA_STR_NICK_NAME);

                        ClientSessMgr.CSMRejectInvitation(0, ulMeetingID,
                                ulUserID, CONSTANTS.REJECT_BUSY);
//                        ClientSessMgr.CSMRejectInvitation(0, ulMeetingID, ulUserID,
//                                strNickName);
                        return;
                    }
                    Log.d(TAG, "CONSTANTS.MEETING_INVITE");
                    Intent tmp = new Intent(context,
//                    MeetingActivity.class);
                            IncomingActivity.class);
                    tmp.putExtras(intent.getExtras());
                    tmp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(tmp);
                }

                break;
        }
//        int flag = intent.getExtras().getInt(CONSTANTS.EXTRA_FLAG);
//        if (flag == CONSTANTS.MEETING_INVITE) {
//            Log.d(TAG, "CONSTANTS.MEETING_INVITE");
//            Intent tmp = new Intent(context,
////                    MeetingActivity.class);
//                    IncomingActivity.class);
//            tmp.putExtras(intent.getExtras());
//            tmp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(tmp);
//        }
    }
}

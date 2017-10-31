package com.zigsun.mobile.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.MeetingBaseItem;
import com.zigsun.bean.MeetingMemberBaseItem;
import com.zigsun.bean.UserInfo;
import com.zigsun.core.AVCaptureNotifyImpl;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.core.ConfControlNotifyImpl;
import com.zigsun.core.ConferenceMgr;
import com.zigsun.core.VideoParam;
import com.zigsun.core.WBNotifyImpl;
import com.zigsun.mobile.interfaces.IDialMeeting;
import com.zigsun.mobile.interfaces.UserStatus;
import com.zigsun.mobile.module.DialingUserInfo;
import com.zigsun.mobile.module.LifeFragment;
import com.zigsun.mobile.module.UserInfoStatus;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.ui.MainHandler;
import com.zigsun.util.CONSTANTS;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Luo on 2015/6/19.
 * 语音/视频会议模块
 */
public class MeetingModel implements IDialMeeting, LifeFragment.Life {
    private static final String TAG = MeetingModel.class.getSimpleName();
    private static final long timeBase = 1432721981636L;
    private static final String TAG_LIFE_FRAGMENT = TAG + ".TAG_LIFE_FRAGMENT";

    private final DialingStatus listener;
    private final Context context;

    private MainHandler mainHandler = EMeetingApplication.getHandler();
    private DialingBroadCastReceiver receiver;
    private Handler handler;


    /**
     * @param activity 需要实现LoginListener接口
     */
    public MeetingModel(FragmentActivity activity, Handler handler) {
        try {
            listener = (DialingStatus) activity;
        } catch (Exception e) {
            throw new ClassCastException("ooooops ..activity must implement DialingStatus");
        }
        context = activity;
        this.handler = handler;
        final LifeFragment lifeFragment = new LifeFragment(this);
//        lifeFragment.setLife(this);
        activity.getSupportFragmentManager().beginTransaction().add(lifeFragment, TAG_LIFE_FRAGMENT).commitAllowingStateLoss();
    }


    @Override
    public void dial(String number) {
        createRoom();
        Log.d(TAG, "dail member list: ------------------------" + EMeetingApplication.getUserInfos().size());
        for (UserInfo i : EMeetingApplication.getUserInfos())
            Log.d(TAG, i.getStrUserName() + "  - " + i.getUlUserID());
        Log.d(TAG, "--------------------------------------");

        mainHandler.setMembersToBeInvited(EMeetingApplication.getUserInfos());

        listener.DialingWithNumber(null);
    }

    @Override
    public void accept(long ulMeetingID, long ulUserID, long meetingType) {
        EMeetingApplication.setIsRoomOwner(false);
        ClientSessMgr.CSMAcceptInvitation(0, ulMeetingID, ulUserID);
        //设为全局
        EMeetingApplication.setulMeeetingID(ulMeetingID);
        EMeetingApplication.setMeetingType(meetingType);
        if (meetingType == CONSTANTS.MEETING_TYPE_VIDEO) {
//            startActivity(new Intent(getApplicationContext(),
//                    MeetingActivity.class));
        } else if (meetingType == CONSTANTS.MEETING_TYPE_AUDIO) {
//            startActivity(new Intent(getApplicationContext(),
//                    VoiceConferenceRoomActivity.class));
        }
    }

    @Override
    public void reject(long ulMeetingID, long ulUserID, String strNickName) {
//        ClientSessMgr.CSMRejectInvitation(0, ulMeetingID, ulUserID,
//                strNickName);
        ClientSessMgr.CSMRejectInvitation(0, ulMeetingID,
                ulUserID, CONSTANTS.REJECT_MANUALLY);
    }


    @Override
    public void hangUp() {
        Log.d(TAG, "hangUp meeting id" + EMeetingApplication
                .getulMeetingID());
        if (EMeetingApplication.getIsRoomOwner()) {
            ClientSessMgr.EndMeeting(EMeetingApplication
                    .getulMeetingID());
            ConferenceMgr.LeaveMeeting();
            ClientSessMgr.ExitMeeting(EMeetingApplication
                    .getulMeetingID());
        } else {
            ConferenceMgr.LeaveMeeting();
            ClientSessMgr.ExitMeeting(EMeetingApplication
                    .getulMeetingID());
        }
    }

    // 创建房间
    private void createRoom() {
        EMeetingApplication.setIsRoomOwner(true);
        EMeetingApplication.setMeetingType(CONSTANTS.MEETING_TYPE_AUDIO);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        Log.d(TAG, "time " + System.currentTimeMillis()
                + " :start to create room " + str + "...");
        ClientSessMgr.CreateRoomMetting("room" + str, CONSTANTS.MEETING_TYPE_AUDIO,
                System.currentTimeMillis() - timeBase);
    }

    @Override
    public void onCreate() {
        receiver = new DialingBroadCastReceiver();
        final IntentFilter filter = new IntentFilter(
                CONSTANTS.ACTION_COM_WEATHER_ACCEPT);
        filter.addAction(CONSTANTS.ACTION_UPDATE_USER_STATUS);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void startAudioRecord() {

    }

    @Override
    public void stopAudioRecord() {

    }

    @Override
    public void startVideoRecord() {
        Log.d("MYDEBUG",
                "主机端当前可用视频数：" + ConferenceMgr.JudgeFreeBrocastVideo(1));
        if (ConferenceMgr.JudgeFreeBrocastVideo(1) > 0) {
//            if (!isCameraOpen) {// 如果是关闭的时候
            EMeetingApplication.setWasCameraOpen(true);
            initSurface();
//            } else {
//                EMeetingApplication.setWasCameraOpen(false);
//                cameraHelper.closeCamera(EMeetingApplication
//                        .getUserInfo().getUlUserID());
//                mSurfaceView.setVisibility(View.GONE);
//            }
//            isCameraOpen = !isCameraOpen;
            listener.videoAction(Action.OPEN_SUCCESS);

        } else {
            listener.videoAction(Action.OPEN_FAILURE);
//            Toast.makeText(getApplicationContext(), "当前视频通道被占用",
//                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void stopVideoRecord() {
        EMeetingApplication.setWasCameraOpen(false);
        handler.sendEmptyMessage(CONSTANTS.MESSAGE_WHATE_stopVideoRecord);
        listener.videoAction(Action.CLOSE_SUCCESS);
    }

    @Override
    public void inviteToMeeting(long ulUserID) {
        Log.d(TAG, "inviteToMeeting() ulUserID= " + ulUserID);

        ClientSessMgr.AddMeetingMember(
                EMeetingApplication.getulMeetingID(),
                ulUserID, 3);
        ClientSessMgr.InviteMemberToMeeting(
                EMeetingApplication.getulMeetingID(),
                ulUserID);
        listener.DialingWithNumber(Utils.findUserById(EMeetingApplication.getUserInfos(), ulUserID).getStrUserName());
    }

    @Override
    public void kickOut(long ulUserID) {
        if (EMeetingApplication.getIsRoomOwner()) {
            ClientSessMgr.KickOutMeetingMember(EMeetingApplication
                    .getulMeetingID(), ulUserID);
            ClientSessMgr.DeleteMeetingMember(EMeetingApplication
                    .getulMeetingID(), ulUserID);
            Log.d(TAG, "method ClientSessMgr.DeleteMeetingMember exec......"
                    + "ulMeetingID = " + EMeetingApplication.getulMeetingID()
                    + " UserID = " + ulUserID);
        }
    }

    private class DialingBroadCastReceiver extends BroadcastReceiver {

        private long creatorID;
//        private ArrayList<MeetingMemberBaseItem> membersInMeetingRoom;

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CONSTANTS.ACTION_UPDATE_USER_STATUS:
                    long ulUserID = intent.getLongExtra(CONSTANTS.EXTRA_UL_USER_ID, 0);
                    byte ucStatus = intent.getByteExtra(CONSTANTS.EXTRA_UC_STATUS, (byte) 0);
                    long ulMeetingID = intent.getLongExtra("ulMeetingID", EMeetingApplication.getulMeetingID());
                    Log.d(TAG, "broadcast ACTION_UPDATE_USER_STATUS- id: " + ulUserID + "status: " + ucStatus + " meetingID:" + ulMeetingID);
                    if (EMeetingApplication.getulMeetingID() == ulMeetingID)
                        onUpdateUserStatus(ulUserID, (int) ucStatus);
                    break;
                default:

                    final String toWho = intent.getStringExtra(CONSTANTS.EXTRA_TO_WHO);
                    switch (toWho) {
                        case CONSTANTS.EXTRA_TO_WHO_VALUE_ROOM_OWNER:
                            hostRoom(intent);
                            break;
                        case CONSTANTS.EXTRA_TO_WHO_VALUE_CLIENT:
                            client(intent);
                            break;
                        case CONSTANTS.EXTRA_TO_WHO_VALUE_ALL:
                            all();
                            break;
                        case CONSTANTS.EXTRA_TO_WHO_VALUE_ALL_FOR_UPDATE_VIEW:

                            onUpdateUserStatus(intent.getLongExtra(CONSTANTS.EXTRA_UL_USER_ID, 0),
                                    (int) intent.getLongExtra(CONSTANTS.EXTRA_UC_STATUS, 0));
                            break;
                        case CONSTANTS.EXTRA_TO_WHO_VALUE_CLIENT_SET_SURFACE:
                            clientSetSurface(intent);
                            break;
                        case "allToBroVoice":
                            ConferenceMgr.MTAVBrocastMemberAudio(
                                    EMeetingApplication.getulMeetingID(),
                                    EMeetingApplication.getUserInfo().getUlUserID());
                            break;
                    }
                    break;
            }
        }

        private void clientSetSurface(Intent intent) {
            if (intent.getStringExtra("doWhat").equals("setSurface")) {
                Log.d(TAG, "客户端需要接收音视频......");
                initSurface();
            } else if (intent.getStringExtra("doWhat").equals("rmSurface")) {
                EMeetingApplication.setSurfaceUsed(false);
                handler.sendEmptyMessage(CONSTANTS.MESSAGE_WHATE_SURFACE_GONE);
//                    mSurfaceView.setVisibility(View.GONE);
            } else if (intent.getStringExtra("doWhat").equals(
                    "setShareSurface")) {
                EMeetingApplication.setSurfaceForShare(true);
                initSurface();
            } else if (intent.getStringExtra("doWhat").equals("clearShareSurface")) {
                EMeetingApplication.setSurfaceForShare(false);
                handler.sendEmptyMessage(CONSTANTS.MESSAGE_WHATE_SURFACE_GONE);
//                    mSurfaceView.setVisibility(View.GONE);
            }

        }

        private void all() {
//            if (EMeetingApplication.getIsRoomOwner()) {
            Log.d("MYDEBUG", "房主===开始广播音视频......");
//            ConferenceMgr.MeetingConnect(new VideoParam((byte) 3,
//                    (byte) 4, (byte) 2, (byte) 3, (byte) 1, (byte) 1));
//            ConferenceMgr.MeetingSetCaptureAndControlNotify(
//                    new AVCaptureNotifyImpl(handler),
//                    new ConfControlNotifyImpl(EMeetingApplication
//                            .getHandler()), new WBNotifyImpl(
//                            EMeetingApplication.getHandler()));
//            Log.d(TAG, "creatorID : "
//                    + EMeetingApplication.getUserInfo().getUlUserID());
//            Log.d(TAG, "after MTAVBrocastMemberAudio......");
//            if (EMeetingApplication.getIsRoomOwner()) {
//                ConferenceMgr.MTAVBrocastMemberAudio(
//                        EMeetingApplication.getulMeetingID(),
//                        EMeetingApplication.getUserInfo().getUlUserID());
//            }


            ConferenceMgr.MeetingConnect(new VideoParam((byte) 9,
                    (byte) 4, (byte) 2, (byte) 3, (byte) 1, (byte) 1));
            ConferenceMgr.MeetingSetCaptureAndControlNotify(
                    new AVCaptureNotifyImpl(handler),
                    new ConfControlNotifyImpl(EMeetingApplication
                            .getHandler()), new WBNotifyImpl(
                            EMeetingApplication.getHandler()));
//            initSurface();
//            }
        }

        private void onUpdateUserStatus(long ulUserID, int ucStatus) {
            // 房主的界面更新，这个广播发的很早，记得判空2015-6-8-11:28
            final DialingUserInfo userInfo = Utils.findUserById(EMeetingApplication.getUserInfos(), ulUserID);
            Log.d(TAG, "update user status: " + ucStatus + " userID: " + ulUserID);
            if (userInfo == null) {
                Log.w(TAG, "update user status,but user is null");
                return;
            }
            switch ((int) ucStatus) {
                // ucStatus = 1 用户在线 0 不在线 2在房间
                case CONSTANTS.EXTRA_UC_STATUS_VALUE_OFFLINE:
                    userInfo.setUcStatus((byte) 0);

                    listener.offLineNotify(ulUserID);
//                    Toast.makeText(
//                            context,
//                            userInfo
//                                    .getStrUserName() + "不在线",
//                            Toast.LENGTH_LONG).show();
//                    initViews(membersToBeInvited.size());
                    break;

                case CONSTANTS.EXTRA_UC_STATUS_VALUE_ONLINE:
                    // 之前是0，说明从不在线到上线，可以继续邀请，之前是2说明客户自己退出了房间
                Log.d(TAG, "EXTRA_UC_STATUS_VALUE_ONLINE");
                    if (userInfo.getUcStatus() ==
                            (byte) CONSTANTS.EXTRA_UC_STATUS_VALUE_OFFLINE) {
                        userInfo.setUcStatus((byte) 1);
                        listener.onLineNotify(ulUserID);
//                            initViews(membersToBeInvited.size());
                    } else if (userInfo
                            .getUcStatus() == (byte) CONSTANTS.EXTRA_UC_STATUS_VALUE_IN_MEETING) {
//                        Toast.makeText(context, userInfo.getStrUserName() + "离开了房间", Toast.LENGTH_LONG).show();
                        userInfo.setUcStatus((byte) CONSTANTS.EXTRA_UC_STATUS_VALUE_LEAV_MEETING);
//                        EMeetingApplication.getUserInfos().remove(userInfo);
                        listener.leaveRoomNotify(ulUserID);
//                            membersToBeInvited.remove(i);
//                            initViews(membersToBeInvited.size());
//                            if (numberInRoom != (byte) 0) {
//                                numberInRoom--;
//                                setNumber(numberInRoom,
//                                        membersToBeInvited.size());
//                            }
                    }
                    break;
               /*
               进入会议
                */
                case CONSTANTS.EXTRA_UC_STATUS_VALUE_IN_MEETING:
                    // 之前是1，表示接受了邀请,之前是0收不到邀请，不需要判断
                    userInfo.setUcStatus((byte) 2);
                    listener.joinRoomNotify(ulUserID);
//                    initViews(membersToBeInvited.size());
                    break;
                default:
                    break;
            }
        }

        private void client(Intent intent) {
            if (EMeetingApplication.getIsRoomOwner()) return;
            Log.d(TAG, "客户端===应该执行的方法......toWho = client");

//            if (membersInMeetingRoom == null) {
//                membersInMeetingRoom = new ArrayList<MeetingMemberBaseItem>();
//            }
            final String extra = intent.getStringExtra(CONSTANTS.EXTRA_FLAG);
            switch (extra) {
                case CONSTANTS.EXTRA_FLAG_VALUE_ON_GET_MEETING_MEMBER_BASE_ITEM:
                    MeetingMemberBaseItem baseItem = (MeetingMemberBaseItem) intent
                            .getSerializableExtra(CONSTANTS.EXTRA_MEETING_MEMBER_BASE_ITEM);
                    Log.d(TAG, "客户端收到： 房间成员的信息，用于设置房间界面......" + baseItem.getStrUserName() + " id: " + baseItem.getUlUserID()
                            + "status: " + baseItem.getUcStatus());
                    if (EMeetingApplication.getUserInfo().getUlUserID() != baseItem.getUlUserID()) {
                        EMeetingApplication.addUserInfo(Utils.wrap(baseItem));
                        listener.onLineNotify(baseItem.getUlUserID());
                    }
//                    if (meetingMemberBaseItem.getUlUserID() == creatorID) {
////                    setCreater(meetingMemberBaseItem.getStrUserName());
//                    } else {
//                        int j = 0;
//                        Log.d(TAG, "membersInMeetingRoom====name=="
//                                + meetingMemberBaseItem.getStrUserName());
//                        for (int i = 0; i < membersInMeetingRoom.size(); i++) {
//                            // 去重复
//                            if (meetingMemberBaseItem.getUlUserID() == membersInMeetingRoom
//                                    .get(i).getUlUserID()) {
//                                membersInMeetingRoom.set(i,
//                                        meetingMemberBaseItem);
//                                break;
//                            } else {
//                                j++;
//                            }
//                        }
//                        if (j == membersInMeetingRoom.size()) {
//                            membersInMeetingRoom.add(meetingMemberBaseItem);
////                        initInvitedViews(membersInMeetingRoom.size());
////                        setNumber((byte) 0, membersInMeetingRoom.size());
//                            Log.d(TAG, "客户端房间成员的List保存数==="
//                                    + membersInMeetingRoom.size());
//                        }
//                    }
                    break;
                case CONSTANTS.EXTRA_FLAG_VALUE_ON_GET_MEETING_BASE_ITEM:
                    MeetingBaseItem item = (MeetingBaseItem) intent
                            .getSerializableExtra("oMeetingBase");
//                    setTime(System.currentTimeMillis() - item.getUlStartTime()
//                            - timeBase);
                    Log.d(TAG, "EXTRA_FLAG_VALUE_ON_GET_MEETING_BASE_ITEM: getStrCreatorNickName: " + item.getStrCreatorNickName());

                    EMeetingApplication.addUserInfo(Utils.wrap(item));
                    listener.onLineNotify(item.getUlCreatorID());
                    creatorID = item.getUlCreatorID();
                    break;
                case CONSTANTS.EXTRA_FLAG_VALUE_KICKOUT:
//                Toast.makeText(getApplicationContext(), "你已经离开房间",
//                        Toast.LENGTH_LONG).show();
                    // 被房主请出房间后会议ID置0
                    EMeetingApplication.setulMeeetingID(0);
                    listener.hangUpWithNumber(null);
                    //finish();
                    break;
                case CONSTANTS.EXTRA_FLAG_VALUE_END_MEETING:
//                Toast.makeText(getApplicationContext(), "房主结束了会议",
//                        Toast.LENGTH_LONG).show();
                    // 房主结束会议后客户端会议ID置0
                    EMeetingApplication.setulMeeetingID(0);
                    listener.hangUpWithNumber(null);
//                finish();
                    break;
            }
        }

        private void hostRoom(Intent intent) {
            if (!EMeetingApplication.getIsRoomOwner()) return;
            Log.d(TAG, "房主===应该执行的方法......");
            Log.d(TAG, "发给房主  :  接收到被邀请者的选择广播...");
            int flag = intent.getIntExtra(CONSTANTS.EXTRA_ACCEPT, 3);
            // int flag = intent.getExtras().getInt("accept",3);
            System.out.println("flag====" + flag);
            long userID = intent.getExtras().getLong(CONSTANTS.UL_MEMBER_ID);
            long RejectKind = intent.getExtras().getLong("RejectKind");
            String name = String.valueOf(userID);
            final DialingUserInfo userInfo = Utils.findUserById(EMeetingApplication.getUserInfos(), userID);
            if (userInfo == null) return;
            switch (flag) {
                case CONSTANTS.EXTRA_ACCEPT_VALUE_REJECTED:
//                    Toast.makeText(context, name + "拒绝了会议邀请...",
//                            Toast.LENGTH_LONG).show();

                    Log.d(TAG, "reject: " + RejectKind);

                    /*if (RejectKind == CONSTANTS.REJECT_MANUALLY) {
                        Toast.makeText(context, nickHelper.getNickNameByUserNumber(name) + "拒绝了会议邀请...",
                                Toast.LENGTH_LONG).show();
                    } else*/
                    if (RejectKind != CONSTANTS.REJECT_BUSY) {
                        userInfo.setStatus(UserInfoStatus.Rejected);
                        listener.rejectedWithNumber(name);
                    }/* else if (RejectKind == CONSTANTS.REJECT_BUSY) {
                        Toast.makeText(context, nickHelper.getNickNameByUserNumber(name) + "正在会议中...",
                                Toast.LENGTH_LONG).show();
                    }*/


//                    userInfo.setStatus(UserInfoStatus.Rejected);
//                    listener.rejectedWithNumber(name);
                    break;
                case CONSTANTS.EXTRA_ACCEPT_VALUE_ACCEPTED:// 代表对方接受了自己的会议邀请
//                    Toast.makeText(context, name + "接受了会议邀请...",
//                            Toast.LENGTH_LONG).show();
                    userInfo.setStatus(UserInfoStatus.Accepted);
                    listener.acceptedWithNumber(name);
                    break;
                case 2:

                    break;

                default:
                    break;
            }
        }

    }

    private void initSurface() {
        handler.sendEmptyMessage(CONSTANTS.MESSAGE_WHATE_INIT_SURFACE);

    }
}

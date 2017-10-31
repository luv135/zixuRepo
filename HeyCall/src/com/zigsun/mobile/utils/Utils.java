package com.zigsun.mobile.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.zigsun.bean.MeetingBaseItem;
import com.zigsun.bean.MeetingMemberBaseItem;
import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.module.DialingUserInfo;
import com.zigsun.mobile.module.RecenListItem;
import com.zigsun.mobile.module.RecentNickItem;
import com.zigsun.mobile.ui.base.AnimationListener;
import com.zigsun.ui.contact.SortModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luo on 2015/6/24.
 */
public class Utils {


    private static final String TAG = Utils.class.getSimpleName();

    public static UserInfo wrap(SortModel item) {
        if (item instanceof ContactItem){
            Log.d(TAG, "item instanceof ContactItem");
            return wrap((ContactItem) item);
        }
        UserInfo userInfo = new DialingUserInfo();
        userInfo.setUlUserID(item.getUlUserId());
        userInfo.setStrUserName(item.getName());
        return userInfo;
    }


    public static UserInfo wrap(ContactItem item) {
        UserInfo userInfo = new DialingUserInfo();
        userInfo.setUlUserID(item.getUlUserId());
        userInfo.setStrUserName(item.getName());
        userInfo.setStrEmail(item.getEmail());
        userInfo.setStrTelephone(item.getTel());
        userInfo.setSzDepart(item.getDepart());
        userInfo.setUcStatus(item.getStatus());
        userInfo.setStrNickName(item.getNickName());
        return userInfo;
    }

    public static ContactItem wrapC(UserInfo item) {
        ContactItem sortModel = new ContactItem();
        sortModel.setUlUserId(item.getUlUserID());
        sortModel.setName(item.getStrUserName());
        sortModel.setDepart(item.getSzDepart());
        sortModel.setEmail(item.getStrEmail());
        sortModel.setTel(item.getStrTelephone());
        sortModel.setStatus(item.getUcStatus());
        return sortModel;
    }

    public static UserInfo wrap(MeetingBaseItem item) {
        final UserInfo userInfo = new DialingUserInfo();
        userInfo.setStrUserName(item.getStrCreatorNickName());
        userInfo.setUlUserID(item.getUlCreatorID());
        return userInfo;
    }

    public static UserInfo wrap(MeetingMemberBaseItem baseItem) {
        final UserInfo userInfo = new DialingUserInfo();
        userInfo.setStrUserName(baseItem.getStrUserName());
        userInfo.setUlUserID(baseItem.getUlUserID());
        userInfo.setUcStatus(baseItem.getUcStatus());

        return userInfo;
    }

    public static SortModel wrap(UserInfo item) {
        SortModel sortModel = new SortModel();
        sortModel.setUlUserId(item.getUlUserID());
        sortModel.setName(item.getStrUserName());
        return sortModel;
    }


    public static List<UserInfo> wrap(String[] names, long[] ids,String[]nickName) {
        List<UserInfo> infos = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            UserInfo userInfo = new DialingUserInfo();
            userInfo.setUlUserID(ids[i]);
            userInfo.setStrUserName(names[i]);
            userInfo.setStrNickName(nickName[i]);
            userInfo.setUcStatus((byte) 0);
            infos.add(userInfo);
        }
        return infos;
    }
    public static List<UserInfo> wrap(String[] strings, long[] longs) {
        return null;
    }
    public static void getUsrInfoNamesAndUserId(List<UserInfo> infos, String[] people, long[] userIds) {
        for (int i = 0; i < infos.size(); i++) {
            final UserInfo userInfo = infos.get(i);
            userIds[i] = userInfo.getUlUserID();
            people[i] = userInfo.getStrUserName();
        }
    }

    /**
     * @return maybe null
     */
    public static DialingUserInfo findUserById(List<UserInfo> userInfos, long ulUserID) {
        for (UserInfo i : userInfos) {
            if (i.getUlUserID() == ulUserID) return (DialingUserInfo) i;
        }
        return null;
    }


    /**
     * @return maybe null
     */
    public static SortModel find(List<SortModel> userInfos, long ulUserID) {
        for (SortModel i : userInfos) {
            if (i.getUlUserId() == ulUserID) return i;
        }
        return null;
    }

    public static RecentNickItem wrap(RecenListItem item) {
        final RecentNickItem recentNickItem = new RecentNickItem();

        recentNickItem.setId(item.getId());
        recentNickItem.setUserId(item.getUserId());
        recentNickItem.setUlUserID(item.getUlUserID());
        recentNickItem.setBegin(item.getBegin());
        recentNickItem.setEnd(item.getEnd());
        recentNickItem.setStatus(item.getStatus());
        recentNickItem.setPeople(item.getPeople());
        return recentNickItem;
    }

    public static UserInfo wrap(String tel, long ulUserID) {
        final DialingUserInfo info = new DialingUserInfo();
        info.setStrUserName(tel);
        info.setUlUserID(ulUserID);
        return info;
    }

    public static void setVisibility(View view, int visible) {

    }

    public static DialingUserInfo findUser(List<UserInfo> userInfos, String number) {
        for (UserInfo i : userInfos) {
            if (i.getStrUserName().equals(number) ) return (DialingUserInfo) i;
        }
        return null;
    }


    public enum STATUS {
        ONLINE, OFF, INMEETING, Leave
    }

    public static boolean check(String telephoneNumber) {
//        if(telephoneNumber.length()==16)
        String exp = "1[34568]\\d{9}";
        return telephoneNumber.length() == 11 && telephoneNumber.matches(exp);
//        return false;
    }


    public static void hideView(Context context, final View view,int animaId){
        final Animation animation = AnimationUtils.loadAnimation(context, animaId);
        if (view.getVisibility() == View.VISIBLE && !animation.hasStarted()) {
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
            view.startAnimation(animation);

        }
    }

    public static void showView(Context context,final View view,int animaId) {
        final Animation animation = AnimationUtils.loadAnimation(context, animaId);
        if (view.getVisibility() != View.VISIBLE && !animation.hasStarted()) {
            view.startAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }



}
